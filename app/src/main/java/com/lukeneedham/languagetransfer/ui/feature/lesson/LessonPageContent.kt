package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.common.LessonScaffold
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonPageAboveButtonContent
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonPageBelowButtonContent
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.LessonPageMainButton
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors

@Composable
fun LessonPageContent(
    lesson: CourseLesson,
    uiState: LessonState,
    showDebugLessonControls: Boolean,
    onBack: () -> Unit,
    onMainButtonClick: () -> Unit,
    skipBackward: () -> Unit,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    LessonScaffold(
        lessonNumber = lesson.lessonNumber,
        colors = lesson.colorScheme.toComposeColors(),
        onBack = onBack,
        aboveButtonContent = {
            LessonPageAboveButtonContent(
                state = uiState,
            )
        },
        mainButton = {
            LessonPageMainButton(
                state = uiState,
                onClick = onMainButtonClick,
            )
        },
        belowButtonContent = {
            LessonPageBelowButtonContent(
                state = uiState,
                jumpBackward = skipBackward,
                togglePlaybackSpeed = togglePlaybackSpeed,
                skipToEnd = skipToEnd,
                jumpForward = jumpForward,
                pausepointReporter = pausepointReporter,
                showDebugLessonControls = showDebugLessonControls,
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    LessonPageContent(
        lesson = MockDataLesson.lesson,
        uiState = MockDataLesson.state,
        onBack = {},
        onMainButtonClick = {},
        skipBackward = {},
        togglePlaybackSpeed = {},
        skipToEnd = {},
        jumpForward = {},
        pausepointReporter = MockDataLesson.pausepointReporter,
        showDebugLessonControls = false,
    )
}