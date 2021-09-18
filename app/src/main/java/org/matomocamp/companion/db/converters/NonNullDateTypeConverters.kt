package org.matomocamp.companion.db.converters

import androidx.room.TypeConverter
import java.util.Date

object NonNullDateTypeConverters {
    @JvmStatic
    @TypeConverter
    fun toDate(value: Long): Date = Date(value)

    @JvmStatic
    @TypeConverter
    fun fromDate(value: Date): Long = value.time
}