package com.lukeneedham.languagetransfer.ui.feature.lesson.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.DurationFormatter
import com.lukeneedham.languagetransfer.util.model.Millis

@Composable
fun LessonDurationBar(
    currentPosition: Millis,
    duration: Millis,
    pausepointFractions: List<Float>,
    modifier: Modifier = Modifier
) {
    val color = Colors.glassy
    Column(modifier = modifier) {
        val progressFraction = currentPosition.toFloat() / duration.toFloat()

        LessonProgressBar(
            progressFraction = progressFraction,
            pausepointFractions = pausepointFractions,
            trackColor = color,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            val fontSize = 16.sp
            Text(
                text = DurationFormatter.format(currentPosition),
                color = color,
                fontSize = fontSize,
            )
            Text(
                text = DurationFormatter.format(duration),
                color = color,
                fontSize = fontSize,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Box(
        modifier = Modifier.background(
            Brush.linearGradient(
                listOf(Color.Red, Color.Black)
            )
        )
    ) {
        LessonDurationBar(
            currentPosition = 10, duration = 300, pausepointFractions = listOf(0.4f, 0.7f, 0.9f),
        )
    }
}