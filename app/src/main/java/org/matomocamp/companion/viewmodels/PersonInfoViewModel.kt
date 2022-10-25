package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.model.StatusEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class PersonInfoViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    @Assisted person: Person
) : ViewModel() {

    val events: Flow<PagingData<StatusEvent>> = Pager(PagingConfig(20)) {
        scheduleDao.getEvents(person)
    }.flow.cachedIn(viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(person: Person): PersonInfoViewModel
    }
}