package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PersonsViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    val persons: Flow<PagingData<Person>> = Pager(PagingConfig(20)) {
        scheduleDao.getPersons()
    }.flow.cachedIn(viewModelScope)
}