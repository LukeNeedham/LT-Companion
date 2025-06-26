package com.lukeneedham.languagetransfer.ui.feature.lesson

import com.lukeneedham.languagetransfer.util.model.Millis

/**
 * Represents the different states of the lesson playback.
 */
sealed interface LessonState {
    /**
     * State when a lesson is being loaded.
     */
    object Loading : LessonState

    data class InProgress(
        val currentPosition: Millis,
        val totalDuration: Millis,
        val playingState: PlayingState,
        val pausepointFractions: List<Float>,
        val playbackSpeed: Float,
        val showDebugLessonControls: Boolean,
    ) : LessonState {
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
    }

    object Completed : LessonState

    /**
     * State when an error occurs during playback.
     */
    data class Error(val message: String) : LessonState
}