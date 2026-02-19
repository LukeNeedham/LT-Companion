package com.lukeneedham.languagetransfer.ui.feature.lesson

import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.util.sfx.AppSoundEffectPlayer
import kotlinx.coroutines.CoroutineScope

class LessonSpecificViewModelFactory(
    private val completedLessonRepository: CompletedLessonRepository,
    private val lessonPausepointProviderFactory: LessonPausepointProvider.Factory,
    private val audioPlayerProvider: AudioPlayerProvider,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: AppSoundEffectPlayer,
) {
    fun create(
        lesson: CourseLesson,
        coroutineScope: CoroutineScope,
    ) = LessonSpecificViewModel(
        lesson = lesson,
        coroutineScope = coroutineScope,
        completedLessonRepository = completedLessonRepository,
        lessonPausepointProviderFactory = lessonPausepointProviderFactory,
        audioPlayerProvider = audioPlayerProvider,
        audioLessonRepository = audioLessonRepository,
        soundEffectPlayer = soundEffectPlayer,
    )
}