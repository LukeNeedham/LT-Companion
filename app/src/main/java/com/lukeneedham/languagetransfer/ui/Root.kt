package com.lukeneedham.languagetransfer.ui

import androidx.compose.runtime.Composable
import com.lukeneedham.common.ui.DebugTheme
import com.lukeneedham.common.ui.ProvideDebugSettings
import com.lukeneedham.languagetransfer.ui.navigation.AppNavigation
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.theme.AppTheme

@Composable
fun Root() {
    AppTheme {
        ProvideDebugSettings(theme = DebugTheme(background = Colors.background)) {
            AppNavigation()
        }
    }
}