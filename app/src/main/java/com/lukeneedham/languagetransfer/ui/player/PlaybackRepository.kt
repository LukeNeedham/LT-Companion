package com.lukeneedham.languagetransfer.ui.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Used for communication between the AudioMediaService and the rest of the app.
 * The flow of data is one-way - the service puts data into this repository,
 * and the rest of the app observes it.
 */
class PlaybackRepository {
    private val playingStateMutable = MutableStateFlow<PlayingState?>(null)
    val playingState = playingStateMutable.asStateFlow()

    fun onStateUpdate(state: PlayingState) {
        playingStateMutable.value = state
    }
}