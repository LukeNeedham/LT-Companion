package com.lukeneedham.languagetransfer.ui.feature.lesson.state

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.theme.Colors

@Composable
fun LessonDebugControls(
    speed: Float,
    togglePlaybackSpeed: () -> Unit,
    skipToEnd: () -> Unit,
    jumpForward: () -> Unit,
    pausepointReporter: PausepointReporter,
) {
    val surface = Colors.glassy

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = surface, shape = RoundedCornerShape(20.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(50.dp)
        ) {
            Button(text = "${speed}x", onClick = togglePlaybackSpeed)
            Button(text = "End", onClick = skipToEnd)
            Button(text = "Forward", onClick = jumpForward)
        }

        Text(text = "Pausepoint Edits", fontSize = 15.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(50.dp)
        ) {
            Button(
                text = "Add",
                onClick = { pausepointReporter.add() }
            )
            Button(
                text = "Remove",
                onClick = { pausepointReporter.remove() }
            )
            Button(
                text = "Shift earlier",
                onClick = { pausepointReporter.shiftEarlier() }
            )
            Button(
                text = "Shift later",
                onClick = { pausepointReporter.shiftLater() }
            )
        }
    }
}

@Composable
private fun RowScope.Button(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 12.sp,
        )
    }
}
