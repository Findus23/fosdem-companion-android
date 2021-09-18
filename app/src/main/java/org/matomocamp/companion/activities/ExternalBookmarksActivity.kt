package org.matomocamp.companion.activities

import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import org.matomocamp.companion.R
import org.matomocamp.companion.fragments.ExternalBookmarksListFragment
import org.matomocamp.companion.utils.extractNfcAppData
import org.matomocamp.companion.utils.hasNfcAppData
import org.matomocamp.companion.utils.toBookmarks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExternalBookmarksActivity : SimpleToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val intent = intent
            val bookmarkIds = when {
                intent.hasExtra(EXTRA_BOOKMARK_IDS) -> intent.getLongArrayExtra(EXTRA_BOOKMARK_IDS)
                intent.hasNfcAppData() -> intent.extractNfcAppData().toBookmarks()
                else -> null
            }
            if (bookmarkIds == null) {
                // Invalid data format, exit
                finish()
                return
            }

            supportFragmentManager.commit {
                add<ExternalBookmarksListFragment>(R.id.content,
                        args = ExternalBookmarksListFragment.createArguments(bookmarkIds))
            }
        }
    }

    companion object {
        const val EXTRA_BOOKMARK_IDS = "bookmark_ids"
    }
}