package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonDebugControls
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.AnimatedNullableVisibility
import com.lukeneedham.languagetransfer.util.model.Millis

@Composable
fun LessonPageBelowButtonContent(
    state: LessonState,
    showDebugLessonControls: Boolean,
    jumpBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    onSeek: (Float) -> Unit,
    pausepointReporter: PausepointReporter,
) {
    val durationBarData = when (state) {
        is LessonState.Completed,
        is LessonState.Error -> null

        is LessonState.InProgress -> {
            DurationBarData(
                currentPosition = state.currentPosition,
                totalDuration = state.totalDuration,
                pausepointFractions = state.pausepointFractions,
            )
        }

        LessonState.Loading -> {
            DurationBarData(
                currentPosition = 0,
                totalDuration = 0,
                pausepointFractions = emptyList(),
            )
        }
    }

    val showControls = when (state) {
        is LessonState.Completed,
        is LessonState.Error -> false

        is LessonState.InProgress,
        LessonState.Loading -> true
    }

    val playbackSpeed = when (state) {
        LessonState.Loading,
        is LessonState.Completed,
        is LessonState.Error -> 1f

        is LessonState.InProgress -> state.playbackSpeed
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
                    LessonDebugControls(
                        speed = playbackSpeed,
                        togglePlaybackSpeed = togglePlaybackSpeed,
                        skipToEnd = skipToEnd,
                        jumpBackward = jumpBackward,
                        jumpForward = jumpForward,
                        pausepointReporter = pausepointReporter,
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CutOutGlassyButton(
                            painter = painterResource(R.drawable.ic_replay),
                            contentDescription = "Skip backwards",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable { jumpBackward() }
                        )
                        // Speed toggle button showing current speed
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .background(color = Colors.glassy, shape = CircleShape)
                                .clip(CircleShape)
                                .clickable { togglePlaybackSpeed() }
                        ) {
                            Text(
                                text = "${playbackSpeed}×",
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AnimatedNullableVisibility(
            item = durationBarData,
            transitionSpec = {
                // Entrance: Slide UP from bottom (+height to 0)
                val entrance = slideInVertically(
                    initialOffsetY = { height -> height },
                    animationSpec = tween(600)
                )

                // Exit: Slide DOWN to bottom (0 to +height)
                val exit = slideOutVertically(
                    targetOffsetY = { height -> height },
                    animationSpec = tween(600)
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