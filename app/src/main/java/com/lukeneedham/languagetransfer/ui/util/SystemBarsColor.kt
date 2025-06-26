package com.lukeneedham.languagetransfer.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SystemBarsColor(
    useDarkIcons: Boolean,
) {
    val systemUiController = rememberSystemUiController()
    // Set light icons in system bars
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
        darkIcons = useDarkIcons,
        isNavigationBarContrastEnforced = false,
        transformColorForLightContent = { it },
    )
}