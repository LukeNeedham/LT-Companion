package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.runtime.Composable
import com.lukeneedham.languagetransfer.ui.feature.common.LessonMessage
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.player.PlayingState


@Composable
fun LessonPageAboveButtonContent(
    state: LessonState,
) {
    val message = when (state) {
        is LessonState.InProgress -> {
            val playingState = state.playingState
            when (playingState) {
                is PlayingState.Paused -> when (playingState.reason) {
                    PlayingState.Paused.Reason.Manual -> null
                    PlayingState.Paused.Reason.Auto -> Message(
                        title = "Auto paused",
                        message = "Think for yourself before resuming",
                    )
                }

                is PlayingState.Playing -> null
            }
        }

        is LessonState.Completed -> {
            if (state.hasCompletedCourse) {
                Message(
                    title = "Course completed!",
                    message = "Congratulations - you're done!",
                )
            } else {
                Message(
                    title = "Lesson completed!",
                    message = "Ready for the next one?",
                )
            }
        }

        is LessonState.Error -> {
            Message(
                title = "Something went wrong",
                message = state.message,
            )
        }

        LessonState.Loading -> null
    }

    if (message != null) {
        LessonMessage(
            title = message.title,
            message = message.message,
        )
    }
}

private data class Message(
    val title: String,
    val message: String,
)
