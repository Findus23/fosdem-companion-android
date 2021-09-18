package org.matomocamp.companion.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.matomocamp.companion.R
import org.matomocamp.companion.adapters.EventsAdapter
import org.matomocamp.companion.api.MatomoCampApi
import org.matomocamp.companion.viewmodels.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchResultListFragment : Fragment(R.layout.recyclerview) {

    @Inject
    lateinit var api: MatomoCampApi
    private val viewModel: SearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsAdapter(view.context)
        val holder = RecyclerViewViewHolder(view).apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            setAdapter(adapter)
            emptyText = getString(R.string.no_search_result)
            isProgressBarVisible = true
        }

        api.roomStatuses.observe(viewLifecycleOwner) { statuses ->
            adapter.roomStatuses = statuses
        }
        viewModel.results.observe(viewLifecycleOwner) { result ->
            adapter.submitList((result as? SearchViewModel.Result.Success)?.list)
            holder.isProgressBarVisible = false
        }
    }
}