package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
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

    AnimatedContent(
        targetState = message,
        transitionSpec = {
            val spec = tween<IntOffset>(durationMillis = 1000)
            val entrance = slideIntoContainer(towards = SlideDirection.Left, animationSpec = spec)
            val exit = slideOutOfContainer(towards = SlideDirection.Left, animationSpec = spec)
            entrance.togetherWith(exit)
        },
        label = "MessageAnimation"
    ) { target ->
        // It is important to render this full-size box inside the AnimatedContent
        // This keeps all items the same size, and prevents weird size animations
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (target != null) {
                LessonMessage(
                    title = target.title,
                    message = target.message,
                )
            }
        }
    }
}

private data class Message(
    val title: String,
    val message: String,
)
