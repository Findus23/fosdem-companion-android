package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import org.matomocamp.companion.alarms.AppAlarmManager
import org.matomocamp.companion.db.BookmarksDao
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.StatusEvent
import org.matomocamp.companion.utils.BackgroundWorkScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ExternalBookmarksViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    private val bookmarksDao: BookmarksDao,
    private val alarmManager: AppAlarmManager,
    @Assisted private val bookmarkIds: LongArray
) : ViewModel() {

    val bookmarks: Flow<PagingData<StatusEvent>> =
        Pager(PagingConfig(20)) {
            scheduleDao.getEvents(bookmarkIds)
        }.flow.cachedIn(viewModelScope)

    fun addAll() {
        BackgroundWorkScope.launch {
            bookmarksDao.addBookmarks(bookmarkIds).let { alarmInfos ->
                alarmManager.onBookmarksAdded(alarmInfos)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bookmarkIds: LongArray): ExternalBookmarksViewModel
    }
}