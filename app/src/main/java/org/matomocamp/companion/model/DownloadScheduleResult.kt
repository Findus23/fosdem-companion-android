package org.matomocamp.companion.model

sealed class DownloadScheduleResult {
    data class Success(val eventsCount: Int) : DownloadScheduleResult()
    object Error : DownloadScheduleResult()
    object UpToDate : DownloadScheduleResult()
}