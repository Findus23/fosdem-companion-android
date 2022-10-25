package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.model.Track
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class TrackScheduleEventViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    @Assisted day: Day,
    @Assisted track: Track
) : ViewModel() {

    val scheduleSnapshot: Deferred<List<Event>> = viewModelScope.async {
        scheduleDao.getEventsWithoutBookmarkStatus(day, track)
    }

    @AssistedFactory
    interface Factory {
        fun create(day: Day, track: Track): TrackScheduleEventViewModel
    }
}