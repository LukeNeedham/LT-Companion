package com.lukeneedham.languagetransfer.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class EventChannel {
    private val channel = Channel<Unit>()
    val flow: Flow<Unit> = channel.receiveAsFlow()

    fun send() {
        channel.trySend(Unit)
    }
}