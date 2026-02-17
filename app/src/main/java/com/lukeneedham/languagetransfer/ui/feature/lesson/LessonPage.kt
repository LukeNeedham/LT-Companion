package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.common.LessonScaffold
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

    LessonScaffold(
        lessonNumber = lesson.lessonNumber,
        colors = lesson.colorScheme.toComposeColors(),
        onBack = onBack,
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
