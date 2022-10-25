package org.matomocamp.companion.db.converters

import androidx.room.TypeConverter
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.model.Track

object GlobalTypeConverters {
    @JvmStatic
    @TypeConverter
    fun fromDay(day: Day): Long = day.index.toLong()

    @JvmStatic
    @TypeConverter
    fun fromTrack(track: Track): Long = track.id

    @JvmStatic
    @TypeConverter
    fun fromPerson(person: Person): Long = person.id

    @JvmStatic
    @TypeConverter
    fun fromEvent(event: Event): Long = event.id
}