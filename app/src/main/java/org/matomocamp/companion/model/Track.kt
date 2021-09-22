package org.matomocamp.companion.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.matomocamp.companion.R
import kotlinx.parcelize.Parcelize

@Entity(tableName = Track.TABLE_NAME, indices = [Index(value = ["name", "type"], name = "track_main_idx", unique = true)])
@Parcelize
data class Track(
        @PrimaryKey
        val id: Long = 0L,
        val name: String,
        val type: Type
) : Parcelable {

    enum class Type(@StringRes @get:StringRes val nameResId: Int,
                    @ColorRes @get:ColorRes val appBarColorResId: Int,
                    @ColorRes @get:ColorRes val statusBarColorResId: Int,
                    @ColorRes @get:ColorRes val textColorResId: Int) {

        other(R.string.other,
                R.color.track_type_other, R.color.track_type_other_dark, R.color.track_type_other_text),
        keynote(R.string.keynote,
                R.color.track_type_keynote, R.color.track_type_keynote_dark, R.color.track_type_keynote_text),
        maintrack(R.string.main_track,
                R.color.track_type_main, R.color.track_type_main_dark, R.color.track_type_main_text),
        devroom(R.string.developer_room,
                R.color.track_type_developer_room, R.color.track_type_developer_room_dark, R.color.track_type_developer_room_text),
        lightningtalk(R.string.lightning_talk,
                R.color.track_type_lightning_talk, R.color.track_type_lightning_talk_dark, R.color.track_type_lightning_talk_text),
        certification(R.string.certification_exam,
                R.color.track_type_certification_exam, R.color.track_type_certification_exam_dark, R.color.track_type_certification_exam_text);

    }

    override fun toString() = name


    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + name.hashCode()
        result = prime * result + type.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Track) return false
        return name == other.name && type == other.type
    }

    companion object {
        const val TABLE_NAME = "tracks"
    }

    val appBarColorResId: Int
        get() = when (name) {
            "Privacy" -> R.color.track_privacy
            "System Administration" -> R.color.track_system_administration
            "Contributing" -> R.color.track_contributing
            "Digital Analytics" -> R.color.track_digital_analytics
            "Using Matomo" -> R.color.track_using_matomo
            "Business" -> R.color.track_business
            "Use Cases" -> R.color.track_business
            "MatomoCamp" -> R.color.track_business
            else -> R.color.track_other
        }

    val textColorResId: Int
        get() = appBarColorResId

    val statusBarColorResId: Int
        get() = when (name) {
            "Privacy" -> R.color.track_privacy_dark
            "System Administration" -> R.color.track_system_administration_dark
            "Contributing" -> R.color.track_contributing_dark
            "Digital Analytics" -> R.color.track_digital_analytics_dark
            "Using Matomo" -> R.color.track_using_matomo_dark
            "Business" -> R.color.track_business_dark
            "Use Cases" -> R.color.track_business_dark
            "MatomoCamp" -> R.color.track_business_dark
            else -> R.color.track_other_dark

        }
}