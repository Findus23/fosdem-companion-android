package org.matomocamp.companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import androidx.paging.toLiveData
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonsViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    val persons: LiveData<PagedList<Person>> = scheduleDao.getPersons().toLiveData(100)
}