package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GradientCheckBox(
    isChecked: Boolean,
    colors: List<Color>,
    modifier: Modifier = Modifier,
) {
    val checkColor = Color.White

    val boxStrokeWidth: Dp = 3.dp // Width of the box stroke
    val boxCornerRadius: Dp = 5.dp // Radius for the box corners

    // Determine the default size of the icon if not provided by modifier
    val iconSize = 40.dp

    Canvas(modifier = modifier.size(iconSize)) {
        val squareSize = size.minDimension // Use the smaller dimension for a perfect square
        val boxRadiusPx = boxCornerRadius.toPx()
        val strokeWidthPx = boxStrokeWidth.toPx()

        when {
            isChecked -> {
                val boxPaint = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )

                val boxTopLeft = Offset(0f, 0f) // Filled box starts at origin
                val boxDrawSize = Size(squareSize, squareSize)
                drawRoundRect(
                    brush = boxPaint,
                    topLeft = boxTopLeft,
                    size = boxDrawSize,
                    cornerRadius = CornerRadius(boxRadiusPx, boxRadiusPx),
                    style = Fill
                )

                val checkPath = Path().apply {
                    // Define the points for the checkmark relative to the canvas size
                    moveTo(
                        squareSize * 0.25f,
                        squareSize * 0.5f
                    ) // Start point (bottom-left of check)
                    lineTo(
                        squareSize * 0.45f,
                        squareSize * 0.7f
                    ) // Mid point (bottom-right of check)
                    lineTo(squareSize * 0.75f, squareSize * 0.3f) // End point (top-right of check)
                }

                drawPath(
                    path = checkPath,
                    color = checkColor,
                    style = Stroke(
                        width = strokeWidthPx * 1.5f, // Make checkmark slightly thicker than box stroke
                        cap = StrokeCap.Round, // Rounded ends for the checkmark lines
                        join = StrokeJoin.Round // Rounded joins for the checkmark corners
                    )
                )
            }

            else -> {
                // Current state: Outline with gradient using provided colors
                val boxPaint = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                )
                val boxDrawingStyle = Stroke(width = strokeWidthPx)
                val boxTopLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
                val boxDrawSize = Size(
                    squareSize - strokeWidthPx,
                    squareSize - strokeWidthPx
                )
                drawRoundRect(
                    brush = boxPaint,
                    topLeft = boxTopLeft,
                    size = boxDrawSize,
                    cornerRadius = CornerRadius(boxRadiusPx, boxRadiusPx),
                    style = boxDrawingStyle
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "CheckBoxIcon States Preview")
@Composable
fun PreviewCheckBoxStates() {
    val gradientBlue = listOf(Color.Blue, Color.Cyan)
    val gradientRed = listOf(Color.Red, Color.Magenta)
    val gradientGreen = listOf(Color.Green, Color.Yellow)
    val gradientGrey = listOf(Color.Gray, Color.Black)
    val gradientSolid = listOf(Color.LightGray, Color.LightGray)

    val gradients = listOf(gradientBlue, gradientRed, gradientGreen, gradientGrey, gradientSolid)

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        gradients.forEach { gradient ->
            GradientCheckBox(
                colors = gradient,
                isChecked = true,
            )
            GradientCheckBox(
                colors = gradient,
                isChecked = false,
            )
        }
    }
}