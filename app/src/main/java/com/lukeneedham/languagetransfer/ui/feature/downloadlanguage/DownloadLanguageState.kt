package com.lukeneedham.languagetransfer.ui.feature.downloadlanguage

sealed interface DownloadLanguageState {
    object Ready : DownloadLanguageState
    data class InProgress(val progressFraction: Float) : DownloadLanguageState
    data class Failed(val message: String) : DownloadLanguageState
    object Done : DownloadLanguageState
}