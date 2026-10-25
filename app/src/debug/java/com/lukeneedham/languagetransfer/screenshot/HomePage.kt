package com.lukeneedham.languagetransfer.screenshot

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.home.HomePageContent
import com.lukeneedham.languagetransfer.ui.feature.home.HomeState
import com.lukeneedham.languagetransfer.ui.feature.home.model.HomeLessonItem
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import java.io.File

/** Rendered by CI to docs/screenshots/<position in order.txt>-HomePage.png: the lesson list, part-way through the course. */
@Preview
@Composable
private fun HomePageScreenshot() {
    fun lesson(number: Int, name: String, colors: List<Long>) = CourseLesson(
        audioFileName = "Lesson $number",
        lessonNumber = number,
        lessonName = name,
        pausepoints = emptyList(),
        audioFile = File(""),
        totalDuration = 1_200_000,
        colorScheme = ColorScheme(colors),
    )
    HomePageContent(
        state = HomeState.Success(
            lessons = listOf(
                HomeLessonItem(lesson(1, "Course Introduction", listOf(0xFFFF6B6B, 0xFFFECE00)), HomeLessonItem.Progress.Completed, hasBookmark = false),
                HomeLessonItem(lesson(2, "Making Sentences", listOf(0xFF7B00FF, 0xFFFF00FF)), HomeLessonItem.Progress.Completed, hasBookmark = false),
                HomeLessonItem(lesson(3, "Asking Questions", listOf(0xFF00B4D8, 0xFF00F5D4)), HomeLessonItem.Progress.Current, hasBookmark = true),
                HomeLessonItem(lesson(4, "Past and Future", listOf(0xFF4361EE, 0xFF4CC9F0)), HomeLessonItem.Progress.Locked, hasBookmark = false),
            ),
        ),
        lessonListState = rememberLazyListState(),
        onLessonClick = {},
        onSettingsClick = {},
    )
}
