package com.lukeneedham.languagetransfer.ui.feature.lesson.state

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonDurationBar
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.theme.Colors

@Composable
fun LessonStateInProgress(
    state: LessonState.InProgress,
    skipBackward: () -> Unit,
    togglePlayPause: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
    modifier: Modifier = Modifier
) {
    val surface = Colors.glassy

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Controls
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            val playingState = state.playingState
            val isAutoPaused = when (playingState) {
                is LessonState.InProgress.PlayingState.Paused -> when (playingState.reason) {
                    LessonState.InProgress.PlayingState.Paused.Reason.Manual -> false
                    LessonState.InProgress.PlayingState.Paused.Reason.Auto -> true
                }

                is LessonState.InProgress.PlayingState.Playing -> false
            }

            val autoPausedMessageAlpha = if (isAutoPaused) 1f else 0f
            // Top
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Hide with alpha so that measuring is consistent
                modifier = Modifier
                    .alpha(autoPausedMessageAlpha)
                    .background(color = surface, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(text = "Auto paused", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "Think for yourself before resuming", fontSize = 14.sp)
            }
            // Center
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                LessonPlayButton(surface, togglePlayPause, state)
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(color = surface)
                    .clickable { skipBackward() }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_replay),
                    contentDescription = "Skip backwards",
                    colorFilter = ColorFilter.tint(color = Color.Black),
                    modifier = Modifier.fillMaxSize(0.5f)
                )
            }
        }

        if (state.showDebugLessonControls) {
            Spacer(modifier = Modifier.height(15.dp))
            LessonDebugControls(
                speed = state.playbackSpeed,
                togglePlaybackSpeed = togglePlaybackSpeed,
                skipToEnd = skipToEnd,
                jumpForward = jumpForward,
                pausepointReporter = pausepointReporter,
            )
        }


        Spacer(modifier = Modifier.height(30.dp))

        LessonDurationBar(
            currentPosition = state.currentPosition,
            duration = state.totalDuration,
            pausepointFractions = state.pausepointFractions,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

