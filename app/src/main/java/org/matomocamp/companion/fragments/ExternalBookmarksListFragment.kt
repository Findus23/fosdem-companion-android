package org.matomocamp.companion.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import org.matomocamp.companion.R
import org.matomocamp.companion.adapters.EventsAdapter
import org.matomocamp.companion.api.MatomoCampApi
import org.matomocamp.companion.settings.UserSettingsProvider
import org.matomocamp.companion.utils.assistedViewModels
import org.matomocamp.companion.utils.launchAndRepeatOnLifecycle
import org.matomocamp.companion.viewmodels.ExternalBookmarksViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExternalBookmarksListFragment : Fragment(R.layout.recyclerview) {

    @Inject
    lateinit var userSettingsProvider: UserSettingsProvider
    @Inject
    lateinit var api: MatomoCampApi
    @Inject
    lateinit var viewModelFactory: ExternalBookmarksViewModel.Factory
    private val viewModel: ExternalBookmarksViewModel by assistedViewModels {
        val bookmarkIds = requireArguments().getLongArray(ARG_BOOKMARK_IDS)!!
        viewModelFactory.create(bookmarkIds)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(REQUEST_KEY_CONFIRM_ADD_ALL) { _, _ -> viewModel.addAll() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsAdapter(view.context)
        val holder = RecyclerViewViewHolder(view).apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            setAdapter(adapter)
            emptyText = getString(R.string.no_bookmark)
            isProgressBarVisible = true
        }

        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.external_bookmarks, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.add_all -> {
                    ConfirmAddAllDialogFragment().show(parentFragmentManager, "confirmAddAll")
                    true
                }
                else -> false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.first { it.refresh !is LoadState.Loading }
            holder.isProgressBarVisible = false
            // Only display the menu items if there is at least one item
            if (adapter.itemCount > 0) {
                requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner)
            }
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
                viewModel.bookmarks.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    class ConfirmAddAllDialogFragment : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.external_bookmarks_add_all_confirmation_title)
                    .setMessage(R.string.external_bookmarks_add_all_confirmation_text)
                    .setPositiveButton(android.R.string.ok) { _, _ -> setFragmentResult(REQUEST_KEY_CONFIRM_ADD_ALL, Bundle()) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
        }
    }

    companion object {
        private const val ARG_BOOKMARK_IDS = "bookmark_ids"
        private const val REQUEST_KEY_CONFIRM_ADD_ALL = "confirm_add_all"

        fun createArguments(bookmarkIds: LongArray) = Bundle(1).apply {
            putLongArray(ARG_BOOKMARK_IDS, bookmarkIds)
        }
    }
}