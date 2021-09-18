package org.matomocamp.companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Person
import org.matomocamp.companion.model.StatusEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PersonInfoViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    private val personLiveData = MutableLiveData<Person>()

    val events: LiveData<PagedList<StatusEvent>> = personLiveData.switchMap { person: Person ->
        scheduleDao.getEvents(person).toLiveData(20)
    }

    fun setPerson(person: Person) {
        if (person != personLiveData.value) {
            personLiveData.value = person
        }
    }
}