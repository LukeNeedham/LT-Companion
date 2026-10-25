package com.lukeneedham.languagetransfer.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonPageContent
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonUiState
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import java.io.File

/** Rendered by CI to docs/screenshots/<position in order.txt>-LessonPage.png: a lesson playing half way through. */
@Preview
@Composable
private fun LessonPageScreenshot() {
    val lesson = CourseLesson(
        audioFileName = "Lesson 3",
        lessonNumber = 3,
        lessonName = "Asking Questions",
        pausepoints = emptyList(),
        audioFile = File(""),
        totalDuration = 1_200_000,
        colorScheme = ColorScheme(listOf(0xFF00B4D8, 0xFF00F5D4)),
    )
    LessonPageContent(
        lesson = lesson,
        uiState = LessonUiState.InProgress(
            currentPosition = lesson.totalDuration / 2,
            totalDuration = lesson.totalDuration,
            playingState = PlayingState.Playing,
            pausepointFractions = emptyList(),
            playbackSpeed = 1f,
        ),
        showDebugLessonControls = false,
        onBack = {},
        onMainButtonClick = {},
        skipBackward = {},
        togglePlaybackSpeed = {},
        skipToEnd = {},
        onSeek = {},
        jumpForward = {},
        pausepointReporter = object : PausepointReporter {
            override fun add() {}
            override fun remove() {}
            override fun shiftLater() {}
            override fun shiftEarlier() {}
        },
    )
}
