package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.theme.Colors

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

        is LessonState.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .background(
                        color = Colors.glassy,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Text(
                    text = "Error loading lesson",
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = state.message,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
            }
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
    }
}