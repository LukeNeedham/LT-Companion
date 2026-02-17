package com.lukeneedham.languagetransfer.ui.feature.lessoncompleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.player.PlaybackRepository
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffect
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.EventChannel
import com.lukeneedham.languagetransfer.util.EventDataChannel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LessonCompletedViewModel(
    private val currentLesson: CourseLesson,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: SoundEffectPlayer,
    private val playbackRepository: PlaybackRepository,
) : ViewModel() {

    private val nextLesson = calculateNextLesson()

    val hasCompletedCourse = nextLesson == null

    private val onReturnToHomeMutable = EventChannel()
    val onReturnToHome = onReturnToHomeMutable.flow

    private val onContinueToNextLessonMutable = EventDataChannel<CourseLesson>()
    val onContinueToNextLesson = onContinueToNextLessonMutable.asSharedFlow()

    init {
        soundEffectPlayer.play(SoundEffect.Completed)

        // Observe unhandled resume events (for example from headphones)
        // and handle them by starting the next lesson
        viewModelScope.launch {
            playbackRepository.resumeChannel.collect {
                onContinueToNextLesson()
            }
        }
    }

    fun onReturnToHome() {
        onReturnToHomeMutable.send()
    }

    fun onContinueToNextLesson() {
        if (nextLesson == null) return
        onContinueToNextLessonMutable.tryEmit(nextLesson)
    }

    private fun calculateNextLesson(): CourseLesson? {
        val courseRes = audioLessonRepository.getLanguageCourse()
        return when (courseRes) {
            is AppResult.Failure -> null
            is AppResult.Success -> {
                val nextLessonNum = currentLesson.lessonNumber + 1
                courseRes.value.getLessonByNumber(nextLessonNum)
            }
        }
    }
} 