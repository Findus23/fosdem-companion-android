package org.matomocamp.companion.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.matomocamp.companion.R
import org.matomocamp.companion.utils.enforceSingleScrollDirection
import org.matomocamp.companion.utils.recyclerView
import org.matomocamp.companion.utils.viewLifecycleLazy
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LiveFragment : Fragment(R.layout.fragment_live), RecycledViewPoolProvider {

    private class ViewHolder(view: View) {
        val pager: ViewPager2 = view.findViewById(R.id.pager)
        val tabs: TabLayout = view.findViewById(R.id.tabs)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = LivePagerAdapter(this)
        ViewHolder(view).apply {
            pager.apply {
                adapter = pagerAdapter
                offscreenPageLimit = 1
                recyclerView.enforceSingleScrollDirection()
            }

            TabLayoutMediator(tabs, pager, false) { tab, position -> tab.text = pagerAdapter.getPageTitle(position) }.attach()
        }
    }

    override val recycledViewPool by viewLifecycleLazy {
        RecyclerView.RecycledViewPool()
    }

    private class LivePagerAdapter(fragment: Fragment)
        : FragmentStateAdapter(fragment.childFragmentManager, fragment.viewLifecycleOwner.lifecycle) {

        private val resources = fragment.resources

        override fun getItemCount() = 3

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> NextLiveListFragment()
            1 -> NowLiveListFragment()
            2 -> AllLiveListFragment()
            else -> throw IllegalStateException()
        }

        fun getPageTitle(position: Int): CharSequence? = when (position) {
            0 -> resources.getString(R.string.next)
            1 -> resources.getString(R.string.now)
            2 -> resources.getString(R.string.all)
            else -> null
        }
    }
}