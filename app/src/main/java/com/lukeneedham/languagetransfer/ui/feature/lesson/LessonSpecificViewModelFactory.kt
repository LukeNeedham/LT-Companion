package com.lukeneedham.languagetransfer.ui.feature.lesson

import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.ui.player.AudioPlayerProvider
import com.lukeneedham.languagetransfer.ui.player.PlaybackRepository
import com.lukeneedham.languagetransfer.ui.util.sfx.SoundEffectPlayer
import com.lukeneedham.languagetransfer.util.DebugOptions
import kotlinx.coroutines.CoroutineScope

class LessonSpecificViewModelFactory(
    private val completedLessonRepository: CompletedLessonRepository,
    private val debugOptions: DebugOptions,
    private val lessonPausepointProviderFactory: LessonPausepointProvider.Factory,
    private val audioPlayerProvider: AudioPlayerProvider,
    private val audioLessonRepository: AudioLessonRepository,
    private val soundEffectPlayer: SoundEffectPlayer,
    private val playbackRepository: PlaybackRepository,
) {
    fun create(
        lesson: CourseLesson,
        coroutineScope: CoroutineScope,
    ) = LessonSpecificViewModel(
        lesson = lesson,
        coroutineScope = coroutineScope,
        completedLessonRepository = completedLessonRepository,
        debugOptions = debugOptions,
        lessonPausepointProviderFactory = lessonPausepointProviderFactory,
        audioPlayerProvider = audioPlayerProvider,
        audioLessonRepository = audioLessonRepository,
        soundEffectPlayer = soundEffectPlayer,
        playbackRepository = playbackRepository,
    )
}