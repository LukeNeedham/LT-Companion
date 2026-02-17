package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.common.LessonHeader
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
            LessonHeader(
                lessonNumber = lesson.lessonNumber,
                onBack = onBack,
            )

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
