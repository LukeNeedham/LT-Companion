package com.lukeneedham.languagetransfer.ui.feature.lesson.state

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.player.PlayingState

@Composable
fun LessonPlayButton(
    surface: Color,
    togglePlayPause: () -> Unit,
    state: PlayingState,
) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_play_pause)
    /** Our AVD is built such that start state is play icon, end state is pause icon */
    val atEnd = state is PlayingState.Paused
    val imagePainter = rememberAnimatedVectorPainter(image, atEnd)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(170.dp)
            // Create a separate layer for blending to work within
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .background(color = surface, shape = CircleShape)
            .clip(CircleShape)
            .clickable { togglePlayPause() }
            .semantics {
                val contDesc = when (state) {
                    is PlayingState.Playing -> "Pause"
                    is PlayingState.Paused -> "Play"
                }
                contentDescription = contDesc
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .drawWithContent {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().apply {
                            blendMode = BlendMode.DstOut
                        }
                        canvas.saveLayer(size.toRect(), paint)
                        with(imagePainter) {
                            draw(size)
                        }
                        canvas.restore()
                    }
                }
        )
    }
}

@Preview
@Composable
internal fun PreviewLessonPlayButton() {
    Box(
        modifier = Modifier.background(color = Color.Red)
    ) {
        LessonPlayButton(
            surface = Color.Blue,
            togglePlayPause = {},
            state = PlayingState.Playing,
        )
    }
}