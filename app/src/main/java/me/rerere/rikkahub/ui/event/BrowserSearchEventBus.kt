package me.rerere.rikkahub.ui.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object BrowserSearchEventBus {
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val events: SharedFlow<String> = _events

    fun emit(query: String) {
        _events.tryEmit(query)
    }
}
