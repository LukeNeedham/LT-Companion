package com.lukeneedham.languagetransfer.domain.model

import android.os.Parcelable
import com.lukeneedham.languagetransfer.ui.util.color.ColorScheme
import com.lukeneedham.languagetransfer.util.model.Millis
import kotlinx.parcelize.Parcelize
import java.io.File

/**
 * Data class representing an audio lesson with its pause points and file path.
 *
 * @property name The name of the audio file (e.g., "Language Transfer - Complete Spanish - Lesson 01.mp3")
 * @property lessonNumber The number of the lesson (e.g., 1, 2, 3)
 * @property pausepoints List of timestamps in milliseconds where the audio should pause for user interaction
 * @property audioFile The file object pointing to the downloaded audio file
 * @property totalDuration The duration of the audio file in milliseconds
 */
@Parcelize
data class CourseLesson(
    val name: String,
    val lessonNumber: Int,
    val pausepoints: List<Millis>,
    val audioFile: File,
    val totalDuration: Millis,
    val colorScheme: ColorScheme,
) : Parcelable
