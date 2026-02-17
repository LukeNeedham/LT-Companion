package com.lukeneedham.languagetransfer.ui.player

sealed interface PlayingState {
    object Playing : PlayingState
    data class Paused(val reason: Reason) : PlayingState {
        enum class Reason {
            Manual,

            /** Playback was paused automatically because a pausepoint was hit */
            Auto,
        }
    }
}