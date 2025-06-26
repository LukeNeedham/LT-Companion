package com.lukeneedham.languagetransfer.ui.feature.startup

import androidx.lifecycle.ViewModel
import com.lukeneedham.languagetransfer.data.network.LanguageDownloadRepository

class StartupViewModel(
    private val languageDownloadRepository: LanguageDownloadRepository,
) : ViewModel() {
    fun hasCourseDownloaded() = languageDownloadRepository.isSpanishCourseDownloaded()
}