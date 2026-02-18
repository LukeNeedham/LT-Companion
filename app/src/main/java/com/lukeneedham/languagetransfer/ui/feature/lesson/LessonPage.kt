package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import com.lukeneedham.languagetransfer.ui.util.WakeLock
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Lesson page where users can play and interact with a specific lesson.
 */
@Composable
fun LessonPage(
    initialLesson: CourseLesson,
    goBack: () -> Unit,
    viewModel: LessonViewModel = koinViewModel { parametersOf(initialLesson) },
) {
    SystemBarsColor(useDarkIcons = false)
    WakeLock()

    val onBackFlow = viewModel.onBack
    LaunchedEffect(onBackFlow) {
        onBackFlow.collect {
            goBack()
        }
    }

    LessonPageContent(
        lesson = viewModel.lesson,
        uiState = viewModel.uiState,
        showDebugLessonControls = viewModel.showDebugLessonControls,
        onBack = viewModel::onBack,
        onMainButtonClick = viewModel::onMainButtonClick,
        skipBackward = viewModel::skipBackward,
        togglePlaybackSpeed = viewModel::togglePlaybackSpeed,
        skipToEnd = viewModel::skipToEnd,
        jumpForward = viewModel::jumpForward,
        pausepointReporter = viewModel.pausepointReporter,
    )
}