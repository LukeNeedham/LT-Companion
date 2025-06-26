package com.lukeneedham.languagetransfer.data.network

import android.content.Context
import com.lukeneedham.languagetransfer.util.AppResult
import java.io.File

/**
 * Service for downloading files from the network
 */
open class LanguageDownloadRepository(
    private val fileDownloader: FileDownloader,
    private val context: Context,
) {
    private val spanishDir = getOutputDir(Dir.spanishDirName)

    fun getSpanishCourseDir() = spanishDir

    /**
     * Checks if the Spanish language course is downloaded
     * @return true if the Spanish course directory exists and contains files, false otherwise
     */
    fun isSpanishCourseDownloaded(): Boolean {
        val dir = getSpanishCourseDir()
        return dir.exists() && dir.isDirectory && dir.listFiles()?.isNotEmpty() ?: false
    }

    open suspend fun downloadSpanishCourse(
        onProgress: (Float) -> Unit,
    ): AppResult<File> {
        return fileDownloader.downloadFile(
            url = "https://downloads.languagetransfer.org/spanish/spanish.zip",
            outputDir = spanishDir,
            onProgress = onProgress,
        )
    }

    private fun getOutputDir(language: String) =
        File(context.filesDir, Dir.baseDir + language)

    private object Dir {
        const val baseDir = "language/"
        const val spanishDirName = "spanish"
    }
}
