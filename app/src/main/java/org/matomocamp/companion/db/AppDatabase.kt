package org.matomocamp.companion.db

import android.content.SharedPreferences
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.matomocamp.companion.alarms.FosdemAlarmManager
import org.matomocamp.companion.db.converters.GlobalTypeConverters
import org.matomocamp.companion.db.entities.Bookmark
import org.matomocamp.companion.db.entities.EventEntity
import org.matomocamp.companion.db.entities.EventTitles
import org.matomocamp.companion.db.entities.EventToPerson
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Link
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.model.Track
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Database(
    entities = [EventEntity::class, EventTitles::class, Person::class, EventToPerson::class,
        Link::class, Track::class, Day::class, Bookmark::class], version = 2, exportSchema = false
)
@TypeConverters(GlobalTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract val scheduleDao: ScheduleDao
    abstract val bookmarksDao: BookmarksDao

    lateinit var sharedPreferences: SharedPreferences
        private set
    lateinit var alarmManager: FosdemAlarmManager
        private set

    override fun init(configuration: DatabaseConfiguration) {
        super.init(configuration)
        // Manual dependency injection
        val entryPoint = EntryPointAccessors.fromApplication(configuration.context, AppDatabaseEntryPoint::class.java)
        sharedPreferences = entryPoint.sharedPreferences
        alarmManager = entryPoint.alarmManager
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AppDatabaseEntryPoint {
        @get:Named("Database")
        val sharedPreferences: SharedPreferences
        val alarmManager: FosdemAlarmManager
    }
}