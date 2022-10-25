package org.matomocamp.companion.widgets

import android.widget.ImageButton
import androidx.lifecycle.LifecycleOwner
import org.matomocamp.companion.R
import org.matomocamp.companion.model.BookmarkStatus
import org.matomocamp.companion.utils.launchAndRepeatOnLifecycle
import org.matomocamp.companion.viewmodels.BookmarkStatusViewModel

/**
 * Connect an ImageButton to a BookmarkStatusViewModel
 * to update its icon according to the current status and trigger a bookmark toggle on click.
 */
fun ImageButton.setupBookmarkStatus(viewModel: BookmarkStatusViewModel, owner: LifecycleOwner) {
    setOnClickListener { viewModel.toggleBookmarkStatus() }
    var previousBookmarkStatus: BookmarkStatus? = null
    owner.launchAndRepeatOnLifecycle {
        viewModel.bookmarkStatus.collect { bookmarkStatus: BookmarkStatus? ->
            if (bookmarkStatus == null) {
                isEnabled = false
                isSelected = false
            } else {
                isEnabled = true
                contentDescription = context.getString(if (bookmarkStatus.isBookmarked) R.string.remove_bookmark else R.string.add_bookmark)
                isSelected = bookmarkStatus.isBookmarked
                // Only animate when the button was showing the status of the same event
                if (bookmarkStatus.eventId != previousBookmarkStatus?.eventId) {
                    jumpDrawablesToCurrentState()
                }
            }
            previousBookmarkStatus = bookmarkStatus
        }
    }
}