package org.matomocamp.companion.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.matomocamp.companion.db.ScheduleDao
import org.matomocamp.companion.flow.stateFlow
import org.matomocamp.companion.flow.versionedResourceFlow
import org.matomocamp.companion.model.Day
import org.matomocamp.companion.model.Track
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

class TracksListViewModel @AssistedInject constructor(
    scheduleDao: ScheduleDao,
    @Assisted day: Day
) : ViewModel() {

    val tracks: Flow<List<Track>> = stateFlow(viewModelScope, null) { subscriptionCount ->
        versionedResourceFlow(scheduleDao.version, subscriptionCount) {
            scheduleDao.getTracks(day)
        }
    }.filterNotNull()

    @AssistedFactory
    interface Factory {
        fun create(day: Day): TracksListViewModel
    }
}