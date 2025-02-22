package org.matomocamp.companion.activities

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.lifecycle.LifecycleOwner
import org.matomocamp.companion.R
import org.matomocamp.companion.api.MatomoCampApi
import org.matomocamp.companion.api.MatomoCampUrls
import org.matomocamp.companion.utils.configureToolbarColors
import org.matomocamp.companion.utils.invertImageColors
import org.matomocamp.companion.utils.isLightTheme
import org.matomocamp.companion.utils.launchAndRepeatOnLifecycle
import org.matomocamp.companion.utils.toSlug
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A special Activity which is displayed like a dialog and shows a room image.
 * Specify the room name and the room image id as Intent extras.
 *
 * @author Christophe Beyls
 */
@AndroidEntryPoint
class RoomImageDialogActivity : AppCompatActivity(R.layout.dialog_room_image) {

    @Inject
    lateinit var api: MatomoCampApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val roomName = intent.getStringExtra(EXTRA_ROOM_NAME)!!
        title = roomName

        findViewById<ImageView>(R.id.room_image).apply {
            if (!context.isLightTheme) {
                invertImageColors()
            }
            setImageResource(intent.getIntExtra(EXTRA_ROOM_IMAGE_RESOURCE_ID, 0))
        }
        configureToolbar(api, this, findViewById(R.id.toolbar), roomName)
    }

    companion object {
        const val EXTRA_ROOM_NAME = "roomName"
        const val EXTRA_ROOM_IMAGE_RESOURCE_ID = "imageResId"

        fun configureToolbar(api: MatomoCampApi, owner: LifecycleOwner, toolbar: Toolbar, roomName: String) {
            toolbar.title = roomName
            if (roomName.isNotEmpty()) {
                val context = toolbar.context

                toolbar.inflateMenu(R.menu.room_image_dialog)
                toolbar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.navigation -> {
                            val localNavigationUrl = MatomoCampUrls.getLocalNavigationToLocation(roomName.toSlug())
                            try {
                                CustomTabsIntent.Builder()
                                        .configureToolbarColors(context, R.color.light_color_primary)
                                        .setShowTitle(true)
                                        .build()
                                        .launchUrl(context, Uri.parse(localNavigationUrl))
                            } catch (ignore: ActivityNotFoundException) {
                            }
                            true
                        }
                        else -> false
                    }
                }

                owner.launchAndRepeatOnLifecycle {
                    // Display the room status as subtitle
                    api.roomStatuses.collect { statuses ->
                        toolbar.subtitle = statuses[roomName]?.let { roomStatus ->
                            SpannableString(context.getString(roomStatus.nameResId)).apply {
                                this[0, length] = ForegroundColorSpan(ContextCompat.getColor(context, roomStatus.colorResId))
                            }
                        }
                    }
                }
            }
        }
    }
}