package com.lukeneedham.languagetransfer.ui.feature.downloadlanguage

import android.util.Log
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
    var state: DownloadLanguageState by mutableStateOf(DownloadLanguageState.Ready)
        private set

    fun download() {
        viewModelScope.launch {
            state = DownloadLanguageState.InProgress(0f)
            val res = languageDownloadRepository.downloadSpanishCourse(
                onProgress = {
                    state = DownloadLanguageState.InProgress(it)
                }
            )
            state = when (res) {
                is AppResult.Failure -> {
                    DownloadLanguageState.Failed(res.error.message ?: "")
                }

                is AppResult.Success -> DownloadLanguageState.Done
            }
        }
    }
}