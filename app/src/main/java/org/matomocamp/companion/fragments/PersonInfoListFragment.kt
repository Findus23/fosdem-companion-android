package org.matomocamp.companion.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.matomocamp.companion.R
import org.matomocamp.companion.adapters.EventsAdapter
import org.matomocamp.companion.api.MatomoCampApi
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.settings.UserSettingsProvider
import org.matomocamp.companion.utils.assistedViewModels
import org.matomocamp.companion.utils.launchAndRepeatOnLifecycle
import org.matomocamp.companion.viewmodels.PersonInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PersonInfoListFragment : Fragment(R.layout.recyclerview) {

    @Inject
    lateinit var userSettingsProvider: UserSettingsProvider
    @Inject
    lateinit var api: MatomoCampApi
    @Inject
    lateinit var viewModelFactory: PersonInfoViewModel.Factory
    private val viewModel: PersonInfoViewModel by assistedViewModels {
        val person: Person = requireArguments().getParcelable(ARG_PERSON)!!
        viewModelFactory.create(person)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsAdapter(view.context)
        val holder = RecyclerViewViewHolder(view).apply {
            recyclerView.apply {
                val contentMargin = resources.getDimensionPixelSize(R.dimen.content_margin)
                setPadding(contentMargin, contentMargin, contentMargin, contentMargin)
                clipToPadding = false
                scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
                layoutManager = LinearLayoutManager(context)
            }
            val concatAdapterConfig = ConcatAdapter.Config.Builder()
                    .setIsolateViewTypes(false)
                    .build()
            setAdapter(ConcatAdapter(concatAdapterConfig, HeaderAdapter(), adapter))
            emptyText = getString(R.string.no_data)
            isProgressBarVisible = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.first { it.refresh !is LoadState.Loading }
            holder.isProgressBarVisible = false
        }

        viewLifecycleOwner.launchAndRepeatOnLifecycle {
            launch {
                userSettingsProvider.zoneId.collect { zoneId ->
                    adapter.zoneId = zoneId
                }
            }
            launch {
                api.roomStatuses.collect { statuses ->
                    adapter.roomStatuses = statuses
                }
            }
            launch {
                viewModel.events.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.ViewHolder>() {

        override fun getItemCount() = 1

        override fun getItemViewType(position: Int) = R.layout.header_person_info

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.header_person_info, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Nothing to bind
        }

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }

    companion object {
        private const val ARG_PERSON = "person"

        fun createArguments(person: Person) = Bundle(1).apply {
            putParcelable(ARG_PERSON, person)
        }
    }
}