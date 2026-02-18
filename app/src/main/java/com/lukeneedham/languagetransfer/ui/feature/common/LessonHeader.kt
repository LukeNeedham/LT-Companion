package com.lukeneedham.languagetransfer.ui.feature.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.ThickText
import com.lukeneedham.languagetransfer.ui.util.ext.cutOut

@Composable
fun LessonHeader(
    lessonNumber: Int,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            // Create a separate layer for blending to work within, so we can use cut-outs
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .background(
                color = Colors.glassy,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 5.dp)
    ) {
        val sideSize = 60.dp
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sideSize)
                .clip(CircleShape)
                .clickable {
                    onBack()
                }
        ) {
            Image(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(35.dp)
                    .cutOut()
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.weight(1f)
        ) {
            AnimatedContent(
                targetState = lessonNumber,
                transitionSpec = {
                    val spec = tween<IntOffset>(durationMillis = 1000)
                    val entrance =
                        slideIntoContainer(towards = SlideDirection.Up, animationSpec = spec)
                    val exit =
                        slideOutOfContainer(towards = SlideDirection.Up, animationSpec = spec)
                    entrance.togetherWith(exit)
                },
                label = "HeaderLessonNumberAnimation"
            ) { target ->
                // Full-size box prevents size-change animations
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 25.dp)
                ) {
                    @Composable
                    fun Side(alpha: Float) {
                        Box(
                            modifier = Modifier.alpha(alpha)
                        ) {
                            Text(
                                text = "#",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.cutOut()
                            )
                            Spacer(modifier = Modifier.width(15.dp))
                        }
                    }

                    Side(alpha = 1f)

                    ThickText(
                        text = "$target",
                        style = TextStyle(
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Black,
                        ),
                        modifier = Modifier.cutOut()
                    )

                    // For centering - invisible
                    Side(alpha = 0f)
                }
            }
        }
        Box(
            modifier = Modifier.size(sideSize)
        )
    }
}

@Preview
@Composable
private fun Preview() {
    LessonHeader(
        lessonNumber = 6,
        onBack = {},
    )
}