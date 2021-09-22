package org.matomocamp.companion.model

import android.os.Parcelable
import androidx.core.os.ConfigurationCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.matomocamp.companion.db.converters.NonNullDateTypeConverters
import org.matomocamp.companion.utils.DateParceler
import org.matomocamp.companion.utils.DateUtils.withMatomoCampTimeZone
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = Day.TABLE_NAME)
@Parcelize
data class Day(
        @PrimaryKey
        val index: Int,
        @field:TypeConverters(NonNullDateTypeConverters::class)
        val date: @WriteWith<DateParceler> Date
) : Comparable<Day>, Parcelable {

    val name: String
        get() {
            return "Day $index (${DAY_DATE_FORMAT.format(date)})"
        }

    val shortName: String
        get() = DAY_DATE_FORMAT.format(date)

    override fun toString() = name

    override fun compareTo(other: Day): Int {
        return index - other.index
    }

    companion object {
        const val TABLE_NAME = "days"

        private val DAY_DATE_FORMAT = SimpleDateFormat("EEEE", Locale.US).withMatomoCampTimeZone()
    }
}