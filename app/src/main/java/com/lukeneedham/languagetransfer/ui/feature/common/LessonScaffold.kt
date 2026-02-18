package com.lukeneedham.languagetransfer.ui.feature.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.ui.feature.lesson.component.AnimatedGradientBackground

/**
 * A shared scaffold for lesson-related pages.
 * It applies the animated gradient background, a common header, and a central content area.
 *
 * Both LessonPage and LessonCompletedPage should use this to ensure identical layout.
 */
@Composable
fun LessonScaffold(
    lessonNumber: Int,
    colors: List<Color>,
    onBack: () -> Unit,
    aboveButtonContent: @Composable () -> Unit,
    mainButton: @Composable () -> Unit,
    belowButtonContent: @Composable () -> Unit,
) {
    AnimatedGradientBackground(colors = colors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            LessonHeader(
                lessonNumber = lessonNumber,
                onBack = onBack,
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(modifier = Modifier.weight(2f), contentAlignment = Alignment.Center) {
                    aboveButtonContent()
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(170.dp)
                ) {
                    mainButton()
                }

                Box(modifier = Modifier.weight(3f), contentAlignment = Alignment.Center) {
                    belowButtonContent()
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}
