package com.lukeneedham.languagetransfer.ui.feature.lesson.component

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.MockDataLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonDebugControls
import com.lukeneedham.languagetransfer.util.model.Millis

@Composable
fun LessonPlaybackControls(
    showDebugLessonControls: Boolean,
    playbackSpeed: Float,
    currentPosition: Millis,
    totalDuration: Millis,
    pausepointFractions: List<Float>,
    jumpBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        if (showDebugLessonControls) {
            Spacer(modifier = Modifier.height(15.dp))
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

        Spacer(modifier = Modifier.weight(1f))

        LessonDurationBar(
            currentPosition = currentPosition,
            duration = totalDuration,
            pausepointFractions = pausepointFractions,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    LessonPlaybackControls(
        showDebugLessonControls = false,
        playbackSpeed = 1f,
        currentPosition = 5000,
        totalDuration = 10000,
        pausepointFractions = emptyList(),
        jumpBackward = {},
        togglePlaybackSpeed = {},
        skipToEnd = {},
        jumpForward = {},
        pausepointReporter = MockDataLesson.pausepointReporter,
    )
}