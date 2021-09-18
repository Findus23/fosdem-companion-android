package org.matomocamp.companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.matomocamp.companion.model.Event

/**
 * ViewModel used for communication between TrackScheduleActivity and TrackScheduleListFragment
 */
class TrackScheduleViewModel : ViewModel() {

    private var _selectedEvent = MutableLiveData<Event?>()
    val selectedEvent: LiveData<Event?> = _selectedEvent

    fun setSelectEvent(event: Event?) {
        if (event != _selectedEvent.value) {
            _selectedEvent.value = event
        }
    }
}