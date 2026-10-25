package com.lukeneedham.languagetransfer.ui.feature.downloadlanguage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun DownloadLanguagePage(
    onContinue: () -> Unit,
    viewModel: DownloadLanguageViewModel = koinViewModel(),
) {
    DownloadLanguagePageContent(
        state = viewModel.state,
        onDownload = viewModel::download,
        onContinue = onContinue,
    )
}
