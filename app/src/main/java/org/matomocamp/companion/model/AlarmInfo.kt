package org.matomocamp.companion.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import org.matomocamp.companion.db.converters.NullableDateTypeConverters
import org.matomocamp.companion.utils.DateParceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.util.Date

@Parcelize
data class AlarmInfo(
        @ColumnInfo(name = "event_id")
        val eventId: Long,
        @ColumnInfo(name = "start_time")
        @field:TypeConverters(NullableDateTypeConverters::class)
        val startTime: @WriteWith<DateParceler> Date?
) : Parcelable