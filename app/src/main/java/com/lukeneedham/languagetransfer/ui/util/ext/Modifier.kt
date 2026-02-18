package com.lukeneedham.languagetransfer.ui.util.ext

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

/**
 * This only works if the parent (to cut out of) has:
 * Modifier.graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
 */
fun Modifier.cutOut(): Modifier = this
    .drawWithContent {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                blendMode = BlendMode.DstOut
            }
            // Create a layer to apply the blend mode to the content of this modifier
            canvas.saveLayer(size.toRect(), paint)
            drawContent()
            canvas.restore()
        }
    }