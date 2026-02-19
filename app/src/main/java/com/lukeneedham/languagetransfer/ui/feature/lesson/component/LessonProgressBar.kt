package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LessonProgressBar(
    progressFraction: Float,
    pausepointFractions: List<Float>,
    trackColor: Color,
) {
    val trackHeightDp = 40.dp
    val fillHeightFraction = 0.6f
    val pausepointHeightFraction = 0.3f

    /** In px */
    val pausepointWidth = 2

    // The outer Box that will draw the "track" and then "cut out" the progress.
    // We use graphicsLayer with a blend mode to achieve the cutting effect.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(trackHeightDp)
            .graphicsLayer(alpha = 0.99f) // Required for BlendMode to work correctly on some platforms
            .drawBehind {
                val trackWidth = size.width
                val trackHeight = size.height

                val fillHeight = trackHeight * fillHeightFraction
                val diffHeight = trackHeight - fillHeight
                val fillPadding = diffHeight / 2

                val trackRadius = trackHeight / 2f

                val pausepointHeight = trackHeight * pausepointHeightFraction

                // Draw the full track (the part that will remain visible)
                drawRoundRect(
                    color = trackColor,
                    cornerRadius = CornerRadius(trackRadius, trackRadius),
                    size = size
                )

                // Calculate the "hole" area
                val maxFillWidth = trackWidth - (fillPadding * 2)
                val filledFillWidth = maxFillWidth * progressFraction

                val holeMaskPath = Path().apply {
                    val radiusSize = fillHeight / 2
                    val radius = CornerRadius(x = radiusSize, y = radiusSize)
                    val rect = RoundRect(
                        left = fillPadding,
                        top = fillPadding,
                        right = fillPadding + maxFillWidth,
                        bottom = fillPadding + fillHeight,
                        topLeftCornerRadius = radius,
                        topRightCornerRadius = radius,
                        bottomRightCornerRadius = radius,
                        bottomLeftCornerRadius = radius,
                    )
                    addRoundRect(rect)
                }
                // Draw the cut-out inside this mask
                clipPath(
                    path = holeMaskPath,
                ) {
                    val left = fillPadding
                    val top = fillPadding
                    val fillRect = Rect(
                        left = left,
                        top = top,
                        right = left + filledFillWidth,
                        bottom = top + fillHeight,
                    )
                    // Create the path for the "hole" (the part that will be transparent)
                    val holeFillPath = Path().apply {
                        addRect(fillRect)
                    }

                    // Draw the hole using BlendMode.DstOut
                    // DstOut means: result is the destination where it does not overlap with the source.
                    // Source (the holePath) will "cut out" from the destination (the track).
                    drawPath(
                        path = holeFillPath,
                        color = Color.Black, // Color doesn't matter for DstOut, but often black is used
                        blendMode = BlendMode.DstOut
                    )
                }

                // Draw cutouts for the pausepoints
                pausepointFractions.forEach { progressFraction ->
                    val halfHeight = pausepointHeight / 2
                    val halfWidth = pausepointWidth / 2

                    val maxFillWidth = trackWidth - (fillPadding * 2)
                    val centerY = center.y
                    val centerX = fillPadding + (maxFillWidth * progressFraction)
                    val rect = Rect(
                        centerX - halfWidth,
                        centerY - halfHeight,
                        centerX + halfWidth,
                        centerY + halfHeight
                    )
                    val path = Path().apply {
                        addRect(rect)
                    }
                    drawPath(
                        path = path,
                        color = Color.Black, // Color doesn't matter for DstOut, but often black is used
                        blendMode = BlendMode.DstOut
                    )
                }
            }
    )
}

@Preview
@Composable
private fun Preview() {
    // Sample pause points at 25%, 50%, and 75% of the progress bar
    val samplePausePoints = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
    Box(
        modifier = Modifier.background(
            Brush.linearGradient(
                listOf(Color.Red, Color.Black)
            )
        )
    ) {
        LessonProgressBar(
            progressFraction = 0.6f,
            pausepointFractions = samplePausePoints,
            trackColor = Color.White,
        )
    }
}