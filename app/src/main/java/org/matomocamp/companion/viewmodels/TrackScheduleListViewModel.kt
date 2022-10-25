package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.flow.schedulerFlow
import org.matomocamp.companion.flow.stateFlow
import org.matomocamp.companion.flow.tickerFlow
import org.matomocamp.companion.flow.versionedResourceFlow
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.StatusEvent
import org.matomocamp.companion.model.Track
import org.matomocamp.companion.utils.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

class TrackScheduleListViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    @Assisted day: Day,
    @Assisted track: Track
) : ViewModel() {

    val schedule: Flow<List<StatusEvent>> = stateFlow(viewModelScope, null) { subscriptionCount ->
        versionedResourceFlow(scheduleDao.bookmarksVersion, subscriptionCount) {
            scheduleDao.getEvents(day, track)
        }
    }.filterNotNull()

    /**
     * @return The current time during the target day, or null outside of the target day.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentTime: Flow<Instant?> = run {
        // Auto refresh during the day passed as argument
        val dayStart = day.date.atStartOfDay(DateUtils.conferenceZoneId).toInstant()
        schedulerFlow(
            dayStart.toEpochMilli(),
            (dayStart + Duration.ofDays(1L)).toEpochMilli()
        )
    }.flatMapLatest { isOn ->
        if (isOn) {
            tickerFlow(TIME_REFRESH_PERIOD).map { Instant.now() }
        } else {
            flowOf(null)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(day: Day, track: Track): TrackScheduleListViewModel
    }

    companion object {
        private val TIME_REFRESH_PERIOD = 1.minutes
    }
}