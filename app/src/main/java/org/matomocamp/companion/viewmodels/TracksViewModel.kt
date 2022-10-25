package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.model.Day
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TracksViewModel @Inject constructor(scheduleDao: ScheduleDao) : ViewModel() {

    val days: Flow<List<Day>> = scheduleDao.days
}