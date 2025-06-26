package com.lukeneedham.languagetransfer.data.repository

import android.media.MediaMetadataRetriever
import android.util.Log
import com.lukeneedham.languagetransfer.data.network.LanguageDownloadRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.model.LanguageCourse
import com.lukeneedham.languagetransfer.ui.util.color.ColorGenerator
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.model.Millis
import java.io.File

/**
 * Repository for accessing audio lessons with their pause points and file paths.
 * This combines the functionality of PausePointsRepository and FileDownloader.
 */
class AudioLessonRepository(
    private val pausePointsRepository: PausePointsRepository,
    private val languageDownloadRepository: LanguageDownloadRepository,
) {
    /** Cache to avoid loading the same data multiple times */
    private val cache = mutableMapOf<String, LanguageCourse>()

    /**
     * Gets a complete language course with all its audio lessons.
     * This includes the pause points and file paths for each lesson.
     *
     * @param language The language to get the course for (e.g., "spanish")
     * @param onDownloading Callback function that is called when audio files are being downloaded
     * @return AppResult containing the LanguageCourse if successful, or an error if not
     */
    fun getLanguageCourse(): AppResult<LanguageCourse> {
        /** Hardcoded to spanish for now */
        val language = "spanish"

        val cached = cache[language]
        if (cached != null) return AppResult.Success(cached)

        // First, get the pause points for the language
        val pausePointsResult = pausePointsRepository.getPausePoints(language)

        when (pausePointsResult) {
            is AppResult.Failure -> {
                return AppResult.Failure(pausePointsResult.error)
            }

            is AppResult.Success -> {
                val languagePausePoints = pausePointsResult.value
                // The directory where audio files for this language should be stored
                val courseDir = languageDownloadRepository.getSpanishCourseDir()

                val files = courseDir.listFiles() ?: emptyArray()
                val lessons = files.sortedBy { it.name }.mapIndexedNotNull { index, file ->
                    val lessonName = file.name
                    val lessonPausepoints = languagePausePoints.audioToPausepoints[lessonName]
                    if (lessonPausepoints == null) {
                        Log.e("AudioLessonRepository", "No pausepoints for $lessonName")
                        return@mapIndexedNotNull null
                    }

                    val lessonNumber = index + 1
                    CourseLesson(
                        name = lessonName,
                        lessonNumber = lessonNumber,
                        pausepoints = lessonPausepoints.sorted(),
                        audioFile = file,
                        totalDuration = getAudioDuration(file),
                        colorScheme = getColorScheme(lessonNumber),
                    )
                }

                if (lessons.isEmpty()) {
                    return AppResult.Failure<LanguageCourse>("No lessons found for $language")
                }

                val course = LanguageCourse(
                    language = language,
                    lessons = lessons
                )
                cache[language] = course
                return AppResult.Success(course)
            }
        }
    }

    /** Deterministically generate a random color scheme for this lesson */
    private fun getColorScheme(lessonNumber: Int) =
        ColorGenerator.generateColorScheme(seed = lessonNumber)

    /**
     * Extracts the duration of an audio file in milliseconds.
     *
     * @param audioFile The audio file to extract duration from
     * @return The duration in milliseconds, or 0 if it couldn't be extracted
     */
    private fun getAudioDuration(audioFile: File): Millis {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(audioFile.absolutePath)

            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()

            durationStr?.toLong() ?: 0
        } catch (e: Exception) {
            Log.e("AudioLessonRepository", "Error extracting duration: ${e.message}")
            0
        }
    }
}
