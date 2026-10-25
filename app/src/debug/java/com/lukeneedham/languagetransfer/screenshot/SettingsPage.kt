package com.lukeneedham.languagetransfer.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.ui.feature.settings.SettingsPageContent

/** Rendered by CI to docs/screenshots/<position in order.txt>-SettingsPage.png: the settings page. */
@Preview
@Composable
private fun SettingsPageScreenshot() {
    SettingsPageContent(onBack = {}, onOpenDebug = {})
}
