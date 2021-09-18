package org.matomocamp.companion.model

import org.matomocamp.companion.livedata.SingleEvent

sealed class LoadingState<out T : Any> {
    /**
     * The current download progress:
     * -1   : in progress, indeterminate
     * 0..99: progress value in percents
     */
    class Loading(val progress: Int = -1) : LoadingState<Nothing>()
    class Idle<T : Any>(val result: SingleEvent<T>) : LoadingState<T>()
}