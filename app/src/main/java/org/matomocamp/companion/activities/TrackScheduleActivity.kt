package org.matomocamp.companion.activities

import android.content.Intent
import android.nfc.NdefRecord
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import org.matomocamp.companion.R
import org.matomocamp.companion.fragments.EventDetailsFragment
import org.matomocamp.companion.fragments.RoomImageDialogFragment
import org.matomocamp.companion.fragments.TrackScheduleListFragment
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.model.Track
import org.matomocamp.companion.utils.CreateNfcAppDataCallback
import org.matomocamp.companion.utils.isLightTheme
import org.matomocamp.companion.utils.setNfcAppDataPushMessageCallbackIfAvailable
import org.matomocamp.companion.utils.setTaskColorPrimary
import org.matomocamp.companion.utils.statusBarColorCompat
import org.matomocamp.companion.utils.tintBackground
import org.matomocamp.companion.utils.toNfcAppData
import org.matomocamp.companion.viewmodels.BookmarkStatusViewModel
import org.matomocamp.companion.viewmodels.TrackScheduleViewModel
import org.matomocamp.companion.widgets.setupBookmarkStatus
import dagger.hilt.android.AndroidEntryPoint

/**
 * Track Schedule container, works in both single pane and dual pane modes.
 *
 * @author Christophe Beyls
 */
@AndroidEntryPoint
class TrackScheduleActivity : AppCompatActivity(R.layout.track_schedule), CreateNfcAppDataCallback {

    private val viewModel: TrackScheduleViewModel by viewModels()
    private val bookmarkStatusViewModel: BookmarkStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val day: Day = intent.getParcelableExtra(EXTRA_DAY)!!
        val track: Track = intent.getParcelableExtra(EXTRA_TRACK)!!

        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            title = track.toString()
            subtitle = day.toString()
        }
        title = "$track, $day"
        val trackType = track.type
        if (isLightTheme) {
            window.statusBarColorCompat = ContextCompat.getColor(this, track.statusBarColorResId)
            val trackAppBarColor = ContextCompat.getColorStateList(this, track.appBarColorResId)!!
            setTaskColorPrimary(trackAppBarColor.defaultColor)
            toolbar.tintBackground(trackAppBarColor)
        } else {
            val trackTextColor = ContextCompat.getColorStateList(this, track.textColorResId)!!
            toolbar.setTitleTextColor(trackTextColor)
        }

        val isTabletLandscape = resources.getBoolean(R.bool.tablet_landscape)

        val fm = supportFragmentManager
        if (savedInstanceState == null) {
            val fromEventId = intent.getLongExtra(EXTRA_FROM_EVENT_ID, -1L)
            val arguments = if (fromEventId != -1L) {
                TrackScheduleListFragment.createArguments(day, track, fromEventId)
            } else {
                TrackScheduleListFragment.createArguments(day, track)
            }
            fm.commit { add<TrackScheduleListFragment>(R.id.schedule, args = arguments) }
        } else {
            // Cleanup after switching from dual pane to single pane mode
            if (!isTabletLandscape) {
                val eventDetailsFragment = fm.findFragmentById(R.id.event)
                val roomImageDialogFragment = fm.findFragmentByTag(RoomImageDialogFragment.TAG)

                if (eventDetailsFragment != null || roomImageDialogFragment != null) {
                    fm.commit {
                        if (eventDetailsFragment != null) {
                            remove(eventDetailsFragment)
                        }
                        if (roomImageDialogFragment != null) {
                            remove(roomImageDialogFragment)
                        }
                    }
                }
            }
        }

        if (isTabletLandscape) {
            // Tablet mode: Show event details in the right pane fragment
            viewModel.selectedEvent.observe(this) { event: Event? ->
                val currentFragment = fm.findFragmentById(R.id.event) as EventDetailsFragment?
                if (event != null) {
                    // Only replace the fragment if the event is different
                    if (currentFragment?.event != event) {
                        // Allow state loss since the event fragment will be synchronized with the list selection after activity re-creation
                        fm.commit {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            replace<EventDetailsFragment>(R.id.event,
                                    args = EventDetailsFragment.createArguments(event))
                        }
                    }
                } else {
                    // Nothing is selected because the list is empty
                    if (currentFragment != null) {
                        fm.commit { remove(currentFragment) }
                    }
                }

                bookmarkStatusViewModel.event = event
            }

            findViewById<ImageButton?>(R.id.fab)?.setupBookmarkStatus(bookmarkStatusViewModel, this)

            // Enable Android Beam
            setNfcAppDataPushMessageCallbackIfAvailable(this)
        }
    }

    override fun getSupportParentActivityIntent(): Intent? {
        return super.getSupportParentActivityIntent()?.apply {
            // Add FLAG_ACTIVITY_SINGLE_TOP to ensure the Main activity in the back stack is not re-created
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }

    // CreateNfcAppDataCallback

    override fun createNfcAppData(): NdefRecord? {
        return viewModel.selectedEvent.value?.toNfcAppData(this)
    }

    companion object {
        const val EXTRA_DAY = "day"
        const val EXTRA_TRACK = "track"

        // Optional extra used as a hint for up navigation from an event
        const val EXTRA_FROM_EVENT_ID = "from_event_id"
    }
}