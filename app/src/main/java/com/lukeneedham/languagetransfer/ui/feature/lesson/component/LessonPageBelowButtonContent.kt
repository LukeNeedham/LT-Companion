package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import com.lukeneedham.common.debug.scale
import com.lukeneedham.common.ui.LocalAnimationSpeed
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonUiState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonDebugControls
import com.lukeneedham.languagetransfer.ui.util.AnimatedNullableVisibility
import com.lukeneedham.languagetransfer.util.model.Millis

@Composable
fun LessonPageBelowButtonContent(
    state: LessonUiState,
    showDebugLessonControls: Boolean,
    jumpBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    onSeek: (Float) -> Unit,
    pausepointReporter: PausepointReporter,
) {
    val durationBarData = when (state) {
        is LessonUiState.Completed,
        is LessonUiState.Error -> null

        is LessonUiState.InProgress -> {
            DurationBarData(
                currentPosition = state.currentPosition,
                totalDuration = state.totalDuration,
                pausepointFractions = state.pausepointFractions,
            )
        }

        LessonUiState.Loading -> {
            DurationBarData(
                currentPosition = 0,
                totalDuration = 0,
                pausepointFractions = emptyList(),
            )
        }
    }

    val showControls = when (state) {
        is LessonUiState.Completed,
        is LessonUiState.Error -> false

        is LessonUiState.InProgress,
        LessonUiState.Loading -> true
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        AnimatedVisibility(
            visible = showControls,
        ) {
            Column {
                Spacer(modifier = Modifier.height(20.dp))

                if (showDebugLessonControls) {
                    val playbackSpeed = when (state) {
                        LessonUiState.Loading,
                        is LessonUiState.Completed,
                        is LessonUiState.Error -> 1f

                        is LessonUiState.InProgress -> state.playbackSpeed
                    }
                    LessonDebugControls(
                        speed = playbackSpeed,
                        togglePlaybackSpeed = togglePlaybackSpeed,
                        skipToEnd = skipToEnd,
                        jumpBackward = jumpBackward,
                        jumpForward = jumpForward,
                        pausepointReporter = pausepointReporter,
                    )
                } else {
                    // Not in debug mode - only replay is available
                    CutOutGlassyButton(
                        painter = painterResource(R.drawable.ic_replay),
                        contentDescription = "Skip backwards",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { jumpBackward() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        val animationSpeed = LocalAnimationSpeed.current
        AnimatedNullableVisibility(
            item = durationBarData,
            transitionSpec = {
                // Entrance: Slide UP from bottom (+height to 0)
                val entrance = slideInVertically(
                    initialOffsetY = { height -> height },
                    animationSpec = tween(animationSpeed.scale(600))
                )

                // Exit: Slide DOWN to bottom (0 to +height)
                val exit = slideOutVertically(
                    targetOffsetY = { height -> height },
                    animationSpec = tween(animationSpeed.scale(600))
                )

                entrance.togetherWith(exit)
            },
            modifier = Modifier.fillMaxWidth()
        ) { target ->
            LessonDurationBar(
                currentPosition = target.currentPosition,
                duration = target.totalDuration,
                pausepointFractions = target.pausepointFractions,
                onSeek = onSeek,
            )
        }
    }
}

private data class DurationBarData(
    val currentPosition: Millis,
    val totalDuration: Millis,
    val pausepointFractions: List<Float>,
)