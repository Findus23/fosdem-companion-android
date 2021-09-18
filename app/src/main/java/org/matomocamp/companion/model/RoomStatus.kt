package org.matomocamp.companion.model

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import org.matomocamp.companion.R

enum class RoomStatus(@StringRes @get:StringRes val nameResId: Int,
                      @ColorRes @get:ColorRes val colorResId: Int) {
    OPEN(R.string.room_status_open, R.color.room_status_open),
    FULL(R.string.room_status_full, R.color.room_status_full),
    EMERGENCY_EVACUATION(R.string.room_status_emergency_evacuation, R.color.room_status_emergency_evacuation)
}