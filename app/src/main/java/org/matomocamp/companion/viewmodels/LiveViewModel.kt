package org.matomocamp.companion.viewmodels

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.livedata.LiveDataFactory
import org.matomocamp.companion.model.StatusEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    private val heartbeat = LiveDataFactory.interval(1L, TimeUnit.MINUTES)

    val nextEvents: LiveData<PagedList<StatusEvent>> = heartbeat.switchMap {
        val now = System.currentTimeMillis()
        scheduleDao.getEventsWithStartTime(now, now + NEXT_EVENTS_INTERVAL).toLiveData(20)
    }

    val eventsInProgress: LiveData<PagedList<StatusEvent>> = heartbeat.switchMap {
        scheduleDao.getEventsInProgress(System.currentTimeMillis()).toLiveData(20)
    }

    companion object {
        private const val NEXT_EVENTS_INTERVAL = 3L * DateUtils.HOUR_IN_MILLIS
    }
}