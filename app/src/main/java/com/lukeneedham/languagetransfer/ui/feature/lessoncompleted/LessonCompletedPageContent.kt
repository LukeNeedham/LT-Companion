package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.KonfettiParty
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import com.lukeneedham.languagetransfer.ui.util.color.ext.toArgbInts
import nl.dionsegijn.konfetti.compose.KonfettiView

@Composable
fun LessonCompletedPageContent(
    lessonNumber: Int,
    hasCompletedCourse: Boolean,
    colorScheme: ColorScheme,
    onReturnToHome: () -> Unit,
    onContinueToNextLesson: () -> Unit,
) {

    val parties = remember {
        KonfettiParty.rain(
            colors = colorScheme.toArgbInts()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.background)
    ) {
        KonfettiView(
            parties = parties,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp)
        ) {
            // Top
            Box(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .background(
                        color = Colors.glassy, shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                val title = if (hasCompletedCourse) {
                    "Course completed!"
                } else {
                    "Lesson ${lessonNumber} Completed!"
                }
                Text(
                    text = title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Congratulations on completing the lesson!",
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontSize = 16.sp,
                )
            }

            // Bottom
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .fillMaxWidth()
                ) {
                    @Composable
                    fun Button(
                        text: String,
                        onClick: () -> Unit,
                        modifier: Modifier = Modifier
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = modifier
                                .fillMaxHeight()
                                .clip(shape = RoundedCornerShape(20.dp))
                                .background(color = Colors.glassy)
                                .clickable { onClick() }
                                .padding(20.dp)
                        ) {
                            Text(text = text, textAlign = TextAlign.Center)
                        }
                    }

                    Button(
                        text = "Return to Overview",
                        onClick = onReturnToHome,
                        modifier = Modifier.weight(1f)
                    )

                    if (!hasCompletedCourse) {
                        Button(
                            text = "Continue to Next Lesson",
                            onClick = onContinueToNextLesson,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val colors = listOf(Color.Black, Color.White).map { it.value.toLong() }
    LessonCompletedPageContent(
        hasCompletedCourse = false,
        onReturnToHome = {},
        onContinueToNextLesson = {},
        lessonNumber = 5,
        colorScheme = ColorScheme(colors),
    )
}