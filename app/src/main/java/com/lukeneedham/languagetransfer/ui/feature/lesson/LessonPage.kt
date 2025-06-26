package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.AnimatedGradientBackground
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonPageStateContent
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import com.lukeneedham.languagetransfer.ui.util.WakeLock
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors
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

    AnimatedGradientBackground(
        colors = lesson.colorScheme.toComposeColors()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .padding(vertical = 20.dp, horizontal = 5.dp)
            ) {
                val sideSize = 60.dp
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(sideSize)
                        .clip(CircleShape)
                        .clickable {
                            onBack()
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "Close",
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Lesson",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${lesson.lessonNumber}",
                        color = Color.Black,
                        fontSize = 35.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Box(
                    modifier = Modifier.size(sideSize)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LessonPageStateContent(
                    state = viewModel.uiState,
                    skipBackward = viewModel::skipBackward,
                    togglePlayPause = viewModel::togglePlayPause,
                    togglePlaybackSpeed = viewModel::togglePlaybackSpeed,
                    onLessonCompleted = onLessonCompleted,
                    skipToEnd = viewModel::skipToEnd,
                    jumpForward = viewModel::jumpForward,
                    pausepointReporter = viewModel.pausepointReporter,
                )
            }
        }
    }
}
