package com.lukeneedham.languagetransfer.ui.feature.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.languagetransfer.R

@Composable
fun LessonHeader(
    lessonNumber: Int,
    onBack: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .background(
                color = Color.White.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
            )
            .padding(vertical = 20.dp, horizontal = 5.dp)
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
                    .size(30.dp)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Lesson",
                color = Color.DarkGray,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "$lessonNumber",
                color = Color.Black,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
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