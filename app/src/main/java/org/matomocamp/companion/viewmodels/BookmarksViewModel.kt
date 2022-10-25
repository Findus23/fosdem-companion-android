package org.matomocamp.companion.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.matomocamp.companion.BuildConfig
import org.matomocamp.companion.alarms.AppAlarmManager
import org.matomocamp.companion.db.BookmarksDao
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.flow.stateFlow
import org.matomocamp.companion.flow.synchronizedTickerFlow
import org.matomocamp.companion.flow.versionedResourceFlow
import org.matomocamp.companion.model.Event
import org.matomocamp.companion.parsers.ExportedBookmarksParser
import org.matomocamp.companion.utils.BackgroundWorkScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarksDao: BookmarksDao,
    private val scheduleDao: ScheduleDao,
    private val alarmManager: AppAlarmManager,
    private val application: Application
) : ViewModel() {

    private val upcomingOnlyStateFlow = MutableStateFlow<Boolean?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val bookmarks: StateFlow<List<Event>?> = stateFlow(viewModelScope, null) { subscriptionCount ->
        upcomingOnlyStateFlow.filterNotNull().flatMapLatest { upcomingOnly ->
            if (upcomingOnly) {
                // Refresh upcoming bookmarks every 2 minutes
                synchronizedTickerFlow(REFRESH_PERIOD, subscriptionCount)
                    .flatMapLatest {
                        getObservableBookmarks(Instant.now() - TIME_OFFSET, subscriptionCount)
                    }
            } else {
                getObservableBookmarks(Instant.EPOCH, subscriptionCount)
            }
        }
    }

    private fun getObservableBookmarks(
        minStartTime: Instant,
        subscriptionCount: StateFlow<Int>
    ): Flow<List<Event>> = versionedResourceFlow(bookmarksDao.version, subscriptionCount) {
        bookmarksDao.getBookmarks(minStartTime)
    }

    var upcomingOnly: Boolean
        get() = upcomingOnlyStateFlow.value == true
        set(value) {
            upcomingOnlyStateFlow.value = value
        }

    fun removeBookmarks(eventIds: LongArray) {
        BackgroundWorkScope.launch {
            if (bookmarksDao.removeBookmarks(eventIds) > 0) {
                alarmManager.onBookmarksRemoved(eventIds)
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun readBookmarkIds(uri: Uri): LongArray = withContext(Dispatchers.IO) {
        val parser = ExportedBookmarksParser(BuildConfig.APPLICATION_ID, scheduleDao.getYear())
        checkNotNull(application.contentResolver.openInputStream(uri)).source().buffer().use {
            parser.parse(it)
        }
    }

    companion object {
        private val REFRESH_PERIOD = 2.minutes

        // In upcomingOnly mode, events that just started are still shown for 5 minutes
        private val TIME_OFFSET = Duration.ofMinutes(5L)
    }
}