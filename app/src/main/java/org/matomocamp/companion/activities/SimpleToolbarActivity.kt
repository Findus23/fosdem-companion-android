package org.matomocamp.companion.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.matomocamp.companion.R

abstract class SimpleToolbarActivity : AppCompatActivity(R.layout.content) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))
    }
}