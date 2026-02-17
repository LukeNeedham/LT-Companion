package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Page displayed when a lesson is completed.
 *
 * @param onReturnToHome Callback to be invoked when the user wants to continue to the next screen
 * @param onContinueToNextLesson Callback to be invoked when the user wants to continue to the next lesson
 */
@Composable
fun LessonCompletedPage(
    lesson: CourseLesson,
    onReturnToHome: () -> Unit,
    onContinueToNextLesson: (nextLesson: CourseLesson) -> Unit,
    viewModel: LessonCompletedViewModel = koinViewModel { parametersOf(lesson) },
) {
    val hasCompletedCourse = viewModel.hasCompletedCourse

    val onReturnToHomeFlow = viewModel.onReturnToHome
    LaunchedEffect(onReturnToHomeFlow) {
        onReturnToHomeFlow.collect {
            onReturnToHome()
        }
    }

    val onContinueToNextLessonFlow = viewModel.onContinueToNextLesson
    LaunchedEffect(onContinueToNextLessonFlow) {
        onContinueToNextLessonFlow.collect {
            onContinueToNextLesson(it)
        }
    }

    SystemBarsColor(useDarkIcons = true)

    LessonCompletedPageContent(
        lessonNumber = lesson.lessonNumber,
        hasCompletedCourse = hasCompletedCourse,
        colorScheme = lesson.colorScheme,
        onReturnToHome = viewModel::onReturnToHome,
        onContinueToNextLesson = viewModel::onContinueToNextLesson

    )
}