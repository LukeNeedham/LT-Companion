package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonDebugControls
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (state.showDebugLessonControls) {
                    Spacer(modifier = Modifier.height(15.dp))
                    LessonDebugControls(
                        speed = state.playbackSpeed,
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

                Spacer(modifier = Modifier.weight(1f))

                LessonDurationBar(
                    currentPosition = state.currentPosition,
                    duration = state.totalDuration,
                    pausepointFractions = state.pausepointFractions,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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

        else -> {}
    }
}