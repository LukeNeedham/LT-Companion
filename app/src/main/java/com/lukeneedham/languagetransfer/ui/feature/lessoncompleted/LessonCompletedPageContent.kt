package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.lukeneedham.languagetransfer.ui.feature.common.LessonScaffold
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.KonfettiParty
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import com.lukeneedham.languagetransfer.ui.util.color.ext.toArgbInts
import com.lukeneedham.languagetransfer.ui.util.color.ext.toComposeColors
import nl.dionsegijn.konfetti.compose.KonfettiView

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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .background(
                        color = Colors.glassy,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 30.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                if (!hasCompletedCourse) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Ready for the next one?",
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        mainButton = {
            if (!hasCompletedCourse) {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_play),
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onContinueToNextLesson() }
                )
            } else {
                CutOutGlassyButton(
                    painter = painterResource(R.drawable.ic_tick),
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
    Box(
        modifier = Modifier.background(Color.Gray)
    ) {
        LessonCompletedPageContent(
            hasCompletedCourse = false,
            onReturnToHome = {},
            onContinueToNextLesson = {},
            lessonNumber = 5,
            colorScheme = ColorScheme(colors),
        )
    }
}