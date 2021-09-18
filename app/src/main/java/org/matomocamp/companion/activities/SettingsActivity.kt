package org.matomocamp.companion.activities

import android.os.Bundle
import androidx.fragment.app.commit
import org.matomocamp.companion.R
import org.matomocamp.companion.fragments.SettingsFragment

class SettingsActivity : SimpleToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            supportFragmentManager.commit { add(R.id.content, SettingsFragment()) }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.partial_zoom_in, R.anim.slide_out_right)
    }
}