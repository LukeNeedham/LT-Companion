package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonState
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonPlayButton
import com.lukeneedham.languagetransfer.ui.player.PlayingState


@Composable
fun LessonPageMainButton(
    state: LessonState,
    onClick: () -> Unit,
) {
    when (state) {
        is LessonState.Loading -> {
            LessonPlayButton(togglePlayPause = {}, state = PlayingState.Playing)
        }

        is LessonState.InProgress -> {
            LessonPlayButton(togglePlayPause = onClick, state = state.playingState)
        }

        is LessonState.Completed -> {
            if (!state.hasCompletedCourse) {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = "Continue",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onClick() }
                )
            } else {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = "Complete",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onClick() }
                )
            }
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
