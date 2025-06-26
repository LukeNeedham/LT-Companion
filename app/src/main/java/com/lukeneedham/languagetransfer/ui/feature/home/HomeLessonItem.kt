package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.feature.home.model.LessonProgress
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.DurationFormatter
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors

@Composable
fun LessonItem(
    lessonProgress: LessonProgress,
    onClick: () -> Unit,
) {
    val lesson = lessonProgress.lesson
    val progress = lessonProgress.progress
    val lessonColors = lesson.colorScheme.toComposeColors()

    val backgroundMod = when (progress) {
        LessonProgress.Progress.Completed,
        LessonProgress.Progress.Locked -> Modifier.background(color = Colors.glassy)

        LessonProgress.Progress.Current -> Modifier.background(
            brush = Brush.linearGradient(lessonColors)
        )
    }

    val verticalPadding = when (progress) {
        LessonProgress.Progress.Locked,
        LessonProgress.Progress.Completed -> 20.dp

        LessonProgress.Progress.Current -> 30.dp
    }

    val textColor = when (progress) {
        LessonProgress.Progress.Locked,
        LessonProgress.Progress.Completed -> Color.Black

        LessonProgress.Progress.Current -> Color.White
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .then(backgroundMod)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = verticalPadding)
    ) {
        Column {
            Text(
                text = "Lesson ${lesson.lessonNumber}",
                fontSize = 20.sp,
                color = textColor,
            )

            Spacer(modifier = Modifier.height(10.dp))

            val duration = DurationFormatter.format(lesson.totalDuration)
            Text(
                text = duration,
                color = textColor.copy(alpha = 0.8f),
                fontSize = 16.sp,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        val isChecked = when (progress) {
            LessonProgress.Progress.Locked,
            LessonProgress.Progress.Current -> false

            LessonProgress.Progress.Completed -> true
        }

        val colors = when (progress) {
            LessonProgress.Progress.Locked -> {
                val color = Color.LightGray
                listOf(color, color)
            }

            LessonProgress.Progress.Current -> {
                val color = Color.White
                listOf(color, color)
            }

            LessonProgress.Progress.Completed -> lesson.colorScheme.toComposeColors()
        }
        GradientCheckBox(
            isChecked = isChecked,
            colors = colors,
            modifier = Modifier.size(30.dp)
        )
    }
}
