package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Event
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class EventViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    @Assisted eventId: Long
) : ViewModel() {

    val event: Deferred<Event?> = viewModelScope.async {
        scheduleDao.getEvent(eventId)
    }

    @AssistedFactory
    interface Factory {
        fun create(eventId: Long): EventViewModel
    }
}