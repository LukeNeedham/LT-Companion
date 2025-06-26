package com.lukeneedham.languagetransfer.ui.util.color.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme

fun ColorScheme.toComposeColors() = colors.map { Color(it) }
fun ColorScheme.toArgbInts() = toComposeColors().map { it.toArgb() }