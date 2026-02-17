package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonPlayButton
import com.lukeneedham.languagetransfer.ui.theme.Colors
import kotlinx.coroutines.delay


@Composable
fun LessonPageMainButton(
    state: LessonState,
    togglePlayPause: () -> Unit,
) {
    when (state) {
        is LessonState.Loading -> {
            var showLoading by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(500)
                showLoading = true
            }
            if (showLoading) {
                CircularProgressIndicator(
                    color = Colors.glassy,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        is LessonState.InProgress -> {
            LessonPlayButton(togglePlayPause = togglePlayPause, state = state.playingState)
        }

        is LessonState.Error -> {
            CutOutGlassyButton(
                painter = painterResource(R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
