package com.lukeneedham.languagetransfer.ui.feature.lesson.state

import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.feature.common.CutOutGlassyButton
import com.lukeneedham.languagetransfer.ui.player.PlayingState

@Composable
fun LessonPlayButton(
    togglePlayPause: () -> Unit,
    state: PlayingState,
) {
    val image = AnimatedImageVector.animatedVectorResource(R.drawable.avd_play_pause)

    /** Our AVD is built such that start state is play icon, end state is pause icon */
    val atEnd = state is PlayingState.Paused
    val imagePainter = rememberAnimatedVectorPainter(image, atEnd)

    CutOutGlassyButton(
        painter = imagePainter,
        modifier = Modifier
            .fillMaxSize()
            .clickable { togglePlayPause() }
            .semantics {
                val contDesc = when (state) {
                    is PlayingState.Playing -> "Pause"
                    is PlayingState.Paused -> "Play"
                }
                contentDescription = contDesc
            }
    )
}

@Preview
@Composable
internal fun PreviewLessonPlayButton() {
    Box(
        modifier = Modifier.background(color = Color.Red)
    ) {
        LessonPlayButton(
            togglePlayPause = {},
            state = PlayingState.Playing,
        )
    }
}