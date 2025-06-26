package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.lifecycle.ViewModel
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.AppResult

class LessonCompletedViewModel(
    private val currentLesson: CourseLesson,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: SoundEffectPlayer,
) : ViewModel() {

    val nextLesson = calculateNextLesson()

    init {
        soundEffectPlayer.play(SoundEffect.Completed)
    }

    private fun calculateNextLesson(): CourseLesson? {
        val course = audioLessonRepository.getLanguageCourse()
        return when (course) {
            is AppResult.Failure -> null
            is AppResult.Success -> {
                val lessons = course.value.lessons
                lessons.firstOrNull {
                    it.lessonNumber == currentLesson.lessonNumber + 1
                }
            }
        }
    }
} 