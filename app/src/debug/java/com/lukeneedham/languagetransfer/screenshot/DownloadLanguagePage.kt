package com.lukeneedham.languagetransfer.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lukeneedham.languagetransfer.ui.feature.downloadlanguage.DownloadLanguagePageContent
import com.lukeneedham.languagetransfer.ui.feature.downloadlanguage.DownloadLanguageState

/** Rendered by CI to docs/screenshots/<position in order.txt>-DownloadLanguagePage.png: the first-run course download, part-way. */
@Preview
@Composable
private fun DownloadLanguagePageScreenshot() {
    DownloadLanguagePageContent(
        state = DownloadLanguageState.InProgress(progressFraction = 0.4f),
        onDownload = {},
        onContinue = {},
    )
}
