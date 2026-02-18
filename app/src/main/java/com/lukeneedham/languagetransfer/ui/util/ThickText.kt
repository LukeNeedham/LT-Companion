package com.lukeneedham.languagetransfer.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ThickText(
    text: String,
    style: TextStyle,
    extraWidth: Float = 5f,
    modifier: Modifier = Modifier
) {
    Box {
        // Filled text
        Text(
            text = text,
            style = style,
            modifier = modifier
        )

        // Extra stroke thickness
        Text(
            text = text,
            style = style.copy(
                drawStyle = Stroke(
                    width = extraWidth,
                    join = StrokeJoin.Round
                )
            ),
            modifier = modifier
        )
    }
}

@Preview
@Composable
private fun Preview() {
    ThickText(
        text = "Test",
        style = TextStyle(),
    )
}