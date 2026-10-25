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

@Composable
internal fun DownloadLanguagePageContent(
    state: DownloadLanguageState,
    onDownload: () -> Unit,
    onContinue: () -> Unit,
) {
    SystemBarsColor(useDarkIcons = true)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        when (state) {
            DownloadLanguageState.Done -> {
                Text(text = "Download completed!")
                Button(
                    onClick = {
                        onContinue()
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Continue")
                }
            }

            is DownloadLanguageState.Failed -> {
                Text(text = "Download failed!")
                Button(
                    onClick = {
                        onDownload()
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Retry")
                }
            }

            is DownloadLanguageState.InProgress -> {
                Text(text = "Download in progress...")
                Spacer(modifier = Modifier.height(20.dp))
                LinearProgressIndicator(
                    progress = { state.progressFraction },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            DownloadLanguageState.Ready -> {
                Text(text = "Ready to download spanish course")
                Button(
                    onClick = {
                        onDownload()
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Download")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    DownloadLanguagePageContent(
        state = DownloadLanguageState.Ready,
        onDownload = {},
        onContinue = {},
    )
}
