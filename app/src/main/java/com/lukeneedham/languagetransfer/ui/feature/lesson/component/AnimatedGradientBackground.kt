package com.lukeneedham.languagetransfer.ui.feature.lesson.component

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

/**
 * A composable that creates a lava lamp-like animated gradient background.
 * The gradient colors slowly move and shift to create a soothing visual effect.
 *
 * @param colors List of colors to use in the gradient. Defaults to a set of warm colors.
 * @param content The content to display on top of the animated background.
 */
@Composable
fun AnimatedGradientBackground(
    colors: List<Color> = listOf(
        Color(0xFFFF5F6D), // Warm red
        Color(0xFFFFC371), // Warm orange
        Color(0xFFFF9A5A), // Warm yellow-orange
        Color(0xFFFF7EB3)  // Warm pink
    ),
    content: @Composable () -> Unit
) {
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
        colors = colors,
        center = Offset(xPosition, yPosition),
        radius = size * 1000f,
        tileMode = TileMode.Mirror
    )
    
    // Apply the gradient as a background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        // Display the content on top of the animated background
        content()
    }
}