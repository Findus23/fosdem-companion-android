package org.matomocamp.companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackScheduleEventViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    private val dayTrackLiveData = MutableLiveData<Pair<Day, Track>>()

    val scheduleSnapshot: LiveData<List<Event>> = dayTrackLiveData.switchMap { (day, track) ->
        liveData {
            emit(scheduleDao.getEventsSnapshot(day, track))
        }
    }

    fun setDayAndTrack(day: Day, track: Track) {
        val dayTrack = day to track
        if (dayTrack != dayTrackLiveData.value) {
            dayTrackLiveData.value = dayTrack
        }
    }
}