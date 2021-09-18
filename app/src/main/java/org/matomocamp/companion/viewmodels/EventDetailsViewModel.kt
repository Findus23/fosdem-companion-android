package org.matomocamp.companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.model.EventDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventDetailsViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    private val eventLiveData = MutableLiveData<Event>()

    val eventDetails: LiveData<EventDetails> = eventLiveData.switchMap { event: Event ->
        scheduleDao.getEventDetails(event)
    }

    fun setEvent(event: Event) {
        if (event != eventLiveData.value) {
            eventLiveData.value = event
        }
    }
}