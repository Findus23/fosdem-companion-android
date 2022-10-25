package org.matomocamp.companion.activities

import android.os.Bundle
import androidx.fragment.app.commit
import org.matomocamp.companion.R
import org.matomocamp.companion.fragments.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : SimpleToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.commit { add(R.id.content, SettingsFragment()) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.partial_zoom_in, R.anim.slide_out_right)
    }
}