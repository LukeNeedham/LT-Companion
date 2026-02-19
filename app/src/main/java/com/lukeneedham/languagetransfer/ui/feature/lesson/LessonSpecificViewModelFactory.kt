package com.lukeneedham.languagetransfer.ui.feature.lesson

import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProviderCache
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.util.sfx.AppSoundEffectPlayer
import com.lukeneedham.languagetransfer.util.DebugOptions
import kotlinx.coroutines.CoroutineScope

class LessonSpecificViewModelFactory(
    private val completedLessonRepository: CompletedLessonRepository,
    private val lessonPausepointProviderCache: LessonPausepointProviderCache,
    private val audioPlayerProvider: AudioPlayerProvider,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: AppSoundEffectPlayer,
    private val debugOptions: DebugOptions,
) {
    fun create(
        lesson: CourseLesson,
        coroutineScope: CoroutineScope,
    ) = LessonSpecificViewModel(
        lesson = lesson,
        coroutineScope = coroutineScope,
        completedLessonRepository = completedLessonRepository,
        lessonPausepointProviderCache = lessonPausepointProviderCache,
        audioPlayerProvider = audioPlayerProvider,
        audioLessonRepository = audioLessonRepository,
        soundEffectPlayer = soundEffectPlayer,
        debugOptions = debugOptions,
    )
}