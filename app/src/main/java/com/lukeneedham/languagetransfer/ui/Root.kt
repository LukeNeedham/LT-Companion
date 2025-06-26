package com.lukeneedham.languagetransfer.ui

import androidx.compose.runtime.Composable
import com.lukeneedham.languagetransfer.ui.navigation.Router
import com.lukeneedham.languagetransfer.ui.theme.LanguageTransferTheme

@Composable
fun Root() {
    LanguageTransferTheme {
        Router()
    }
}