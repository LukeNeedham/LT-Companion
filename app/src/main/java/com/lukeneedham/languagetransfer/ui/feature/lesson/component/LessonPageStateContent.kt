package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonStateInProgress
import com.lukeneedham.languagetransfer.ui.theme.Colors
import kotlinx.coroutines.delay

@Composable
fun LessonPageStateContent(
    state: LessonState,
    skipBackward: () -> Unit,
    togglePlayPause: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    onLessonCompleted: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    when (state) {
        is LessonState.Loading -> {
            // Only show loading indicator if it has been loading for a while,
            // to avoid a flicker

            var showLoading by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(500)
                showLoading = true
            }

            if (showLoading) {
                CircularProgressIndicator(
                    color = Colors.glassy,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        is LessonState.InProgress -> {
            LessonStateInProgress(
                state = state,
                skipBackward = skipBackward,
                togglePlayPause = togglePlayPause,
                togglePlaybackSpeed = togglePlaybackSpeed,
                skipToEnd = skipToEnd,
                jumpForward = jumpForward,
                pausepointReporter = pausepointReporter,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        is LessonState.Completed -> {
            onLessonCompleted()
        }

        is LessonState.Error -> {
            val surface = Colors.glassy

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .background(
                        color = surface,
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
    }
}