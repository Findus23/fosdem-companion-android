package org.matomocamp.companion.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import org.matomocamp.companion.R
import org.matomocamp.companion.api.MatomoCampUrls.localNavigation
import org.matomocamp.companion.utils.configureToolbarColors
import org.matomocamp.companion.utils.invertImageColors
import org.matomocamp.companion.utils.isLightTheme

class MapFragment : Fragment(R.layout.fragment_map) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.map, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
                R.id.directions -> {
                    launchDirections()
                    true
                }
                R.id.navigation -> {
                    launchLocalNavigation()
                    true
                }
                else -> false
            }
        }, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.map).apply {
            if (!context.isLightTheme) {
                invertImageColors()
            }
        }
    }

    private fun launchDirections() {
        // Build intent to start Google Maps directions
        val uri = "https://maps.google.com/maps?f=d&daddr=${DESTINATION_LATITUDE},${DESTINATION_LONGITUDE}&dirflg=r".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(intent)
        } catch (ignore: ActivityNotFoundException) {
        }
    }

    private fun launchLocalNavigation() {
        try {
            val context = requireContext()
            CustomTabsIntent.Builder()
                    .configureToolbarColors(context, R.color.light_color_primary)
                    .setShowTitle(true)
                    .build()
                    .launchUrl(context, Uri.parse(localNavigation))
        } catch (ignore: ActivityNotFoundException) {
        }
    }

    companion object {
        private const val DESTINATION_LATITUDE = 50.812375
        private const val DESTINATION_LONGITUDE = 4.380734
    }
}