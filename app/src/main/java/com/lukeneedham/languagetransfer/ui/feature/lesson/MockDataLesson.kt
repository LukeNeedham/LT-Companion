package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.ui.graphics.Color
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import java.io.File

object MockDataLesson {
    val colors = listOf(Color.Red, Color.Blue).map { it.value.toLong() }
    val colorScheme = ColorScheme(colors)

    val lesson = CourseLesson(
        name = "Lesson 1",
        lessonNumber = 1,
        pausepoints = emptyList(),
        audioFile = File(""),
        totalDuration = 10000,
        colorScheme = colorScheme
    )

    val state = LessonState.InProgress(
        currentPosition = lesson.totalDuration / 2,
        totalDuration = lesson.totalDuration,
        playingState = PlayingState.Playing,
        pausepointFractions = emptyList(),
        playbackSpeed = 1f,
    )

    val pausepointReporter = object : PausepointReporter {
        override fun add() {}
        override fun remove() {}
        override fun shiftLater() {}
        override fun shiftEarlier() {}
    }
}