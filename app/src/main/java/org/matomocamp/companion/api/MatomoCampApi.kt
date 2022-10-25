package org.matomocamp.companion.api

import android.os.SystemClock
import androidx.annotation.MainThread
import org.matomocamp.companion.alarms.AppAlarmManager
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.flow.flowWhileShared
import org.matomocamp.companion.flow.schedulerFlow
import org.matomocamp.companion.flow.stateFlow
import org.matomocamp.companion.model.DownloadScheduleResult
import org.matomocamp.companion.model.LoadingState
import org.matomocamp.companion.model.RoomStatus
import org.matomocamp.companion.parsers.EventsParser
import org.matomocamp.companion.parsers.RoomStatusesParser
import org.matomocamp.companion.utils.BackgroundWorkScope
import org.matomocamp.companion.utils.ByteCountSource
import org.matomocamp.companion.utils.DateUtils
import org.matomocamp.companion.utils.network.HttpClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.buffer
import java.time.LocalTime
import java.util.concurrent.TimeUnit
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
    private val alarmManager: AppAlarmManager
) {
    private var downloadJob: Job? = null
    private val _downloadScheduleState =
        MutableStateFlow<LoadingState<DownloadScheduleResult>>(LoadingState.Idle())

    /**
     * Download & store the schedule to the database.
     * Only a single Job will be active at a time.
     * The result will be notified through downloadScheduleState StateFlow.
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

    private suspend fun downloadScheduleInternal() {
        _downloadScheduleState.value = LoadingState.Loading()
        val res = try {
            val response = httpClient.get(MatomoCampUrls.schedule, scheduleDao.lastModifiedTag.first()) { body, headers ->
                val length = body.contentLength()
                val source = if (length > 0L) {
                    // Broadcast the progression in percents, with a precision of 1/10 of the total file size
                    ByteCountSource(body.source(), length / 10L) { byteCount ->
                        // Cap percent to 100
                        val percent = (byteCount * 100L / length).toInt().coerceAtMost(100)
                        _downloadScheduleState.value = LoadingState.Loading(percent)
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
            DownloadScheduleResult.Error
        }
        _downloadScheduleState.value = LoadingState.Idle(res)
    }

    val downloadScheduleState: StateFlow<LoadingState<DownloadScheduleResult>> =
        _downloadScheduleState.asStateFlow()

    fun downloadScheduleResultConsumed() {
        _downloadScheduleState.update { state ->
            if (state is LoadingState.Idle) LoadingState.Idle() else state
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val roomStatuses: Flow<Map<String, RoomStatus>> by lazy(LazyThreadSafetyMode.NONE) {
        // Implementors: replace the above code block with the next line to disable room status support
         emptyFlow()
    }


    companion object {
        private val DAY_START_TIME = LocalTime.of(8, 30)
        private val DAY_END_TIME = LocalTime.of(19, 0)
        private val ROOM_STATUS_REFRESH_DELAY = TimeUnit.SECONDS.toMillis(90L)
        private val ROOM_STATUS_FIRST_RETRY_DELAY = TimeUnit.SECONDS.toMillis(30L)
        private val ROOM_STATUS_EXPIRATION_DELAY = TimeUnit.MINUTES.toMillis(6L)
    }
}