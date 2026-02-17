package com.lukeneedham.languagetransfer.ui.feature.lesson

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.common.LessonScaffold
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonDurationBar
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonDebugControls
import com.lukeneedham.languagetransfer.ui.feature.lesson.state.LessonPlayButton
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import com.lukeneedham.languagetransfer.ui.util.WakeLock
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Lesson page where users can play and interact with a specific lesson.
 *
 * @param lesson The audio lesson to play
 * @param onBack Callback to be invoked when the user wants to go back
 * @param onLessonCompleted Callback to be invoked when the lesson is completed
 * @param viewModel The view model for this page
 */
@Composable
fun LessonPage(
    lesson: CourseLesson,
    onBack: () -> Unit,
    onLessonCompleted: () -> Unit,
    viewModel: LessonViewModel = koinViewModel { parametersOf(lesson) },
) {
    SystemBarsColor(useDarkIcons = false)
    WakeLock()

    LessonScaffold(
        lessonNumber = lesson.lessonNumber,
        colors = lesson.colorScheme.toComposeColors(),
        onBack = onBack,
        aboveButtonContent = {
            LessonPageAboveButtonContent(
                state = viewModel.uiState,
            )
        },
        mainButton = {
            LessonPageMainButton(
                state = viewModel.uiState,
                togglePlayPause = viewModel::togglePlayPause,
            )
        },
        belowButtonContent = {
            LessonPageBelowButtonContent(
                state = viewModel.uiState,
                skipBackward = viewModel::skipBackward,
                togglePlaybackSpeed = viewModel::togglePlaybackSpeed,
                skipToEnd = viewModel::skipToEnd,
                jumpForward = viewModel::jumpForward,
                pausepointReporter = viewModel.pausepointReporter,
            )
        }
    )
}

@Composable
private fun LessonPageAboveButtonContent(
    state: LessonState,
) {
    val inProgressState = state as? LessonState.InProgress ?: return
    val playingState = inProgressState.playingState
    val isAutoPaused = when (playingState) {
        is PlayingState.Paused -> when (playingState.reason) {
            PlayingState.Paused.Reason.Manual -> false
            PlayingState.Paused.Reason.Auto -> true
        }

        is PlayingState.Playing -> false
    }

    if (isAutoPaused) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(color = Colors.glassy, shape = RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Auto paused",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = "Think for yourself before resuming", fontSize = 14.sp)
        }
    }
}

@Composable
private fun LessonPageMainButton(
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
                modifier = Modifier.fillMaxSize()
            )
        }

        is LessonState.Completed -> {
            // Handled by navigation
        }
    }
}

@Composable
private fun LessonPageBelowButtonContent(
    state: LessonState,
    skipBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    when (state) {
        is LessonState.InProgress -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color = Colors.glassy)
                        .clickable { skipBackward() }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_replay),
                        contentDescription = "Skip backwards",
                        colorFilter = ColorFilter.tint(color = Color.Black),
                        modifier = Modifier.fillMaxSize(0.5f)
                    )
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
