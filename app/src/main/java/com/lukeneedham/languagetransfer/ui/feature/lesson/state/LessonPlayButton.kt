package com.lukeneedham.languagetransfer.ui.feature.lesson.state

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState

@Composable
fun LessonPlayButton(
    surface: Color,
    togglePlayPause: () -> Unit,
    state: LessonState.InProgress
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(color = surface)
            .clickable { togglePlayPause() }
    ) {
        val contDesc = when (state.playingState) {
            is LessonState.InProgress.PlayingState.Playing -> "Pause"
            is LessonState.InProgress.PlayingState.Paused -> "Play"
        }
        val imageRes = when (state.playingState) {
            is LessonState.InProgress.PlayingState.Playing -> R.drawable.ic_pause
            is LessonState.InProgress.PlayingState.Paused -> R.drawable.ic_play
        }
        Image(
            painter = painterResource(imageRes),
            contentDescription = contDesc,
            colorFilter = ColorFilter.tint(color = Color.Black),
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}