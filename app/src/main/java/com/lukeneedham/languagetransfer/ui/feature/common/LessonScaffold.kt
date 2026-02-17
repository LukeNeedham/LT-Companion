package com.lukeneedham.languagetransfer.ui.feature.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
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
    content: @Composable () -> Unit,
) {
    AnimatedGradientBackground(colors = colors) {
        Box(Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(20.dp)
            ) {
                LessonHeader(
                    lessonNumber = lessonNumber,
                    onBack = onBack,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    content()
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
