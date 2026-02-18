package com.lukeneedham.languagetransfer.ui.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T> AnimatedNullableVisibility(
    item: T?,
    transitionSpec: AnimatedContentTransitionScope<T?>.() -> ContentTransform,
    modifier: Modifier,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = item,
        transitionSpec = transitionSpec,
        contentKey = {
            // Only animate the change between null and non-null
            it != null
        },
        label = "AnimatedNullableVisibility"
    ) { targetItem ->
        Box(modifier = modifier) {
            if (targetItem != null) {
                content(targetItem)
            }
        }
    }
}