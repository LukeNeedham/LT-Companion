package com.lukeneedham.languagetransfer.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun <T> EventChannel() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_LATEST
)
