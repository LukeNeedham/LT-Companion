package com.lukeneedham.languagetransfer.ui.feature.startup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartupPage(
    continueToHome: () -> Unit,
    continueToCourseDownload: () -> Unit,
    viewModel: StartupViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        val isDownloaded = viewModel.hasCourseDownloaded()
        if (isDownloaded) {
            continueToHome()
        } else {
            continueToCourseDownload()
        }
    }
}