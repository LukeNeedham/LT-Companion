package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * A composable that creates a lava lamp-like animated gradient background.
 * The gradient colors slowly move and shift to create a soothing visual effect.
 *
 * @param colors List of colors to use in the gradient. Defaults to a set of warm colors.
 * @param content The content to display on top of the animated background.
 */
@Composable
fun AnimatedGradientBackground(
    colors: List<Color>,
    content: @Composable () -> Unit
) {
    // For previews
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            content()
        }
    }

    // Animate each color in the list when colors param changes
    val animatedColors = colors.map { targetColor ->
        animateColorAsState(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = 1000),
            label = "colorAnimation"
        ).value
    }

    // Create infinite transitions for the animation
    val infiniteTransition = rememberInfiniteTransition(label = "gradientTransition")

    // Animate the x position of the gradient
    val xPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "xPosition"
    )

    // Animate the y position of the gradient
    val yPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "yPosition"
    )

    // Animate the size of the gradient
    val size by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "size"
    )

    // Create the gradient brush with animated properties
    val brush = Brush.radialGradient(
        colors = animatedColors,
        center = Offset(xPosition, yPosition),
        radius = size * 1000f,
        tileMode = TileMode.Mirror
    )

    // Apply the gradient as a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Optimization: drawBehind for frequently changing animations
            .drawBehind {
                drawRect(brush)
            }
    ) {
        // Display the content on top of the animated background
        content()
    }
}