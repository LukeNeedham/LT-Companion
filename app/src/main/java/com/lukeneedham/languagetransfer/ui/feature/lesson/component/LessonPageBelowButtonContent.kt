package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.runtime.Composable
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter

@Composable
fun LessonPageBelowButtonContent(
    state: LessonState,
    jumpBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    when (state) {
        is LessonState.InProgress -> {
            LessonPlaybackControls(
                showDebugLessonControls = state.showDebugLessonControls,
                playbackSpeed = state.playbackSpeed,
                currentPosition = state.currentPosition,
                totalDuration = state.totalDuration,
                pausepointFractions = state.pausepointFractions,
                jumpBackward = jumpBackward,
                togglePlaybackSpeed = togglePlaybackSpeed,
                skipToEnd = skipToEnd,
                jumpForward = jumpForward,
                pausepointReporter = pausepointReporter,
            )
        }

        LessonState.Loading -> {
            // Show the skeleton controls
            LessonPlaybackControls(
                showDebugLessonControls = false,
                playbackSpeed = 1f,
                currentPosition = 0,
                totalDuration = 0,
                pausepointFractions = emptyList(),
                jumpBackward = {},
                togglePlaybackSpeed = {},
                skipToEnd = {},
                jumpForward = {},
                pausepointReporter = pausepointReporter,
            )
        }

        is LessonState.Completed -> {
            // No controls below button on completion screen
        }

        is LessonState.Error -> {
            // No controls
        }
    }
}