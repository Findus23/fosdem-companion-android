package org.matomocamp.companion.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.matomocamp.companion.db.converters.GlobalTypeConverters
import org.matomocamp.companion.db.entities.Bookmark
import org.matomocamp.companion.db.entities.EventEntity
import org.matomocamp.companion.db.entities.EventTitles
import org.matomocamp.companion.db.entities.EventToPerson
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Link
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.model.Track

@Database(
    entities = [EventEntity::class, EventTitles::class, Person::class, EventToPerson::class,
        Link::class, Track::class, Day::class, Bookmark::class], version = 3, exportSchema = false
)
@TypeConverters(GlobalTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val scheduleDao: ScheduleDao
    abstract val bookmarksDao: BookmarksDao

    // Manually injected fields, used by Daos
    lateinit var dataStore: DataStore<Preferences>
}