package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.feature.common.LessonMessage
import com.lukeneedham.languagetransfer.ui.feature.common.LessonScaffold
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors

@Composable
fun LessonCompletedPageContent(
    lessonNumber: Int,
    hasCompletedCourse: Boolean,
    colorScheme: ColorScheme,
    onReturnToHome: () -> Unit,
    onContinueToNextLesson: () -> Unit,
) {
    LessonScaffold(
        lessonNumber = lessonNumber,
        colors = colorScheme.toComposeColors(),
        onBack = onReturnToHome,
        aboveButtonContent = {
            val title = if (hasCompletedCourse) {
                "Course completed!"
            } else {
                "Lesson completed!"
            }

            LessonMessage(
                title = title,
                message = "Ready for the next one?",
            )
        },
        mainButton = {
            if (!hasCompletedCourse) {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = "Continue",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onContinueToNextLesson() }
                )
            } else {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_tick),
                    contentDescription = "Complete",
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onReturnToHome() }
                )
            }
        },
        belowButtonContent = {},
    )
}

@Preview
@Composable
private fun Preview() {
    val colors = listOf(Color.Red, Color.Blue).map { it.value.toLong() }
    LessonCompletedPageContent(
        hasCompletedCourse = false,
        onReturnToHome = {},
        onContinueToNextLesson = {},
        lessonNumber = 5,
        colorScheme = ColorScheme(colors),
    )
}