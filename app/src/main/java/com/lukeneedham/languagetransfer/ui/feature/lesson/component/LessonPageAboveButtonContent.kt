package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.runtime.Composable
import com.lukeneedham.languagetransfer.ui.feature.common.LessonMessage
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.player.PlayingState


@Composable
fun LessonPageAboveButtonContent(
    state: LessonState,
) {
    val inProgressState = state as? LessonState.InProgress ?: return
    val playingState = inProgressState.playingState
    val isAutoPaused = when (playingState) {
        is PlayingState.Paused -> when (playingState.reason) {
            PlayingState.Paused.Reason.Manual -> false
            PlayingState.Paused.Reason.Auto -> true
        }

        is PlayingState.Playing -> false
    }

    if (isAutoPaused) {
        LessonMessage(
            title = "Auto paused",
            message = "Think for yourself before resuming",
        )
    }
}
