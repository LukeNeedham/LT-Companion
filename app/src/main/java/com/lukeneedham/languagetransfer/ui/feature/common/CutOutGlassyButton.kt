package com.lukeneedham.languagetransfer.ui.feature.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.ext.cutOut

@Composable
fun CutOutGlassyButton(
    /** The painter used for the 'cut-out' shape */
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val surface = Colors.glassy

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            // Create a separate layer for blending to work within
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .background(color = surface, shape = CircleShape)
            .clip(CircleShape)
            .then(modifier)
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize(0.6f)
                .cutOut()
        )
    }
}

@Preview
@Composable
private fun Preview() {
    CutOutGlassyButton(
        painter = painterResource(R.drawable.ic_play),
        contentDescription = null,
    )
}