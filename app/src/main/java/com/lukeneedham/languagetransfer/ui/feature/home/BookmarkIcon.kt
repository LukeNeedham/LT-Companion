package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R

@Composable
fun BookmarkIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_bookmark),
            contentDescription = "Bookmarked",
            colorFilter = ColorFilter.tint(Color.LightGray),
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxHeight()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    BookmarkIcon()
}