package com.lukeneedham.languagetransfer.ui.feature.downloadlanguage

sealed interface DownloadLanguageUiState {
    object Ready : DownloadLanguageUiState
    data class InProgress(val progressFraction: Float) : DownloadLanguageUiState
    data class Failed(val message: String) : DownloadLanguageUiState
    object Done : DownloadLanguageUiState
}