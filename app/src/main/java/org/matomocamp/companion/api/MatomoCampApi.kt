package org.matomocamp.companion.api

import android.os.SystemClock
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import org.matomocamp.companion.alarms.FosdemAlarmManager
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.livedata.LiveDataFactory.scheduler
import org.matomocamp.companion.livedata.SingleEvent
import org.matomocamp.companion.model.DownloadScheduleResult
import org.matomocamp.companion.model.LoadingState
import org.matomocamp.companion.model.RoomStatus
import org.matomocamp.companion.parsers.EventsParser
import org.matomocamp.companion.parsers.RoomStatusesParser
import org.matomocamp.companion.utils.BackgroundWorkScope
import org.matomocamp.companion.utils.ByteCountSource
import org.matomocamp.companion.utils.network.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.buffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

/**
 * Main API entry point.
 *
 * @author Christophe Beyls
 */
@Singleton
class MatomoCampApi @Inject constructor(
    private val httpClient: HttpClient,
    private val scheduleDao: ScheduleDao,
    private val alarmManager: FosdemAlarmManager
) {
    private var downloadJob: Job? = null
    private val _downloadScheduleState = MutableLiveData<LoadingState<DownloadScheduleResult>>()

    /**
     * Download & store the schedule to the database.
     * Only a single Job will be active at a time.
     * The result will be sent back through downloadScheduleResult LiveData.
     */
    @MainThread
    fun downloadSchedule(): Job {
        // Returns the download job in progress, if any
        return downloadJob ?: BackgroundWorkScope.launch {
            downloadScheduleInternal()
            downloadJob = null
        }.also {
            downloadJob = it
        }
    }

    @MainThread
    private suspend fun downloadScheduleInternal() {
        _downloadScheduleState.value = LoadingState.Loading()
        val res = try {
            val response = httpClient.get(MatomoCampUrls.schedule, scheduleDao.lastModifiedTag) { body, headers ->
                val length = body.contentLength()
                val source = if (length > 0L) {
                    // Broadcast the progression in percents, with a precision of 1/10 of the total file size
                    ByteCountSource(body.source(), length / 10L) { byteCount ->
                        // Cap percent to 100
                        val percent = (byteCount * 100L / length).toInt().coerceAtMost(100)
                        _downloadScheduleState.postValue(LoadingState.Loading(percent))
                    }.buffer()
                } else {
                    body.source()
                }

                val events = EventsParser().parse(source)
                scheduleDao.storeSchedule(events, headers.get(HttpClient.LAST_MODIFIED_HEADER_NAME))
            }
            when (response) {
                is HttpClient.Response.NotModified -> DownloadScheduleResult.UpToDate    // Nothing parsed, the result is up-to-date
                is HttpClient.Response.Success -> {
                    alarmManager.onScheduleRefreshed()
                    DownloadScheduleResult.Success(response.body)
                }
            }
        } catch (e: Exception) {
            Log.e("MatomoCamp", "Download Error", e)
            DownloadScheduleResult.Error
        }
        _downloadScheduleState.value = LoadingState.Idle(SingleEvent(res))
    }

    val downloadScheduleState: LiveData<LoadingState<DownloadScheduleResult>>
        get() = _downloadScheduleState

    val roomStatuses: LiveData<Map<String, RoomStatus>> by lazy(LazyThreadSafetyMode.NONE) {
        // The room statuses will only be loaded when the event is live.
        // Use the days from the database to determine it.
        val scheduler = scheduleDao.days.switchMap { days ->
            val startEndTimestamps = LongArray(days.size * 2)
            var index = 0
            for (day in days) {
                val dayStart = day.date.time
                startEndTimestamps[index++] = dayStart + DAY_START_TIME
                startEndTimestamps[index++] = dayStart + DAY_END_TIME
            }
            scheduler(*startEndTimestamps)
        }
        // Implementors: replace the above code block with the next line to disable room status support
         MutableLiveData()
    }


    companion object {
        // 8:30 (local time)
        private const val DAY_START_TIME = 8 * DateUtils.HOUR_IN_MILLIS + 30 * DateUtils.MINUTE_IN_MILLIS

        // 19:00 (local time)
        private const val DAY_END_TIME = 19 * DateUtils.HOUR_IN_MILLIS
    }
}