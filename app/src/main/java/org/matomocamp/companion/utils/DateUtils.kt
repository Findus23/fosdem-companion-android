package org.matomocamp.companion.utils

import android.content.Context
import androidx.preference.PreferenceManager
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    val matomoCampTimeZone: TimeZone = TimeZone.getTimeZone("Europe/Berlin")

    val localOffset = TimeZone.getDefault().getOffset(2021, 11, 5, 0, 0, 0)
    val matomoCampOffset = matomoCampTimeZone.getOffset(2021, 11, 5, 0, 0, 0)

    fun DateFormat.withMatomoCampTimeZone(): DateFormat {
        timeZone = matomoCampTimeZone
        return this
    }

    fun DateFormat.withLocalTimeZone(): DateFormat {
        timeZone = TimeZone.getDefault()
        return this
    }

    fun getTimeDateFormat(context: Context): DateFormat {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val useLocalTimeZone = sharedPreferences.getBoolean(PreferenceKeys.USE_LOCAL_TIMEZONE, false);
        val timeFormat = android.text.format.DateFormat.getTimeFormat(context)
        return if (useLocalTimeZone) {
            timeFormat.withLocalTimeZone()
        } else {
            timeFormat.withMatomoCampTimeZone()
        }
    }

    fun getYear(timestamp: Long, calendar: Calendar = Calendar.getInstance(matomoCampTimeZone, Locale.US)): Int {
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.YEAR)
    }
}