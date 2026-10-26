package com.lukeneedham.languagetransfer.ui.feature.downloadlanguage

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.data.network.LanguageDownloadRepository
import com.lukeneedham.languagetransfer.util.AppResult
import kotlinx.coroutines.launch

class DownloadLanguageViewModel(
    private val languageDownloadRepository: LanguageDownloadRepository,
) : ViewModel() {
    var state: DownloadLanguageUiState by mutableStateOf(DownloadLanguageUiState.Ready)
        private set

    fun download() {
        viewModelScope.launch {
            state = DownloadLanguageUiState.InProgress(0f)
            val res = languageDownloadRepository.downloadSpanishCourse(
                onProgress = {
                    state = DownloadLanguageUiState.InProgress(it)
                }
            )
            state = when (res) {
                is AppResult.Failure -> {
                    DownloadLanguageUiState.Failed(res.error.message ?: "")
                }

                is AppResult.Success -> DownloadLanguageUiState.Done
            }
        }
    }
}