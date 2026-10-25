package com.lukeneedham.languagetransfer.ui.feature.lesson

import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.util.model.Millis

/**
 * Represents the different states of the lesson playback.
 */
sealed interface LessonUiState {
    /**
     * State when a lesson is being loaded.
     */
    object Loading : LessonUiState

    data class InProgress(
        val currentPosition: Millis,
        val totalDuration: Millis,
        val playingState: PlayingState,
        val pausepointFractions: List<Float>,
        val playbackSpeed: Float,
    ) : LessonUiState

    /**
     * State when an error occurs during playback.
     */
    data class Error(val message: String) : LessonUiState

    /**
     * State when a lesson is completed.
     */
    data class Completed(
        val hasCompletedCourse: Boolean,
    ) : LessonUiState
}