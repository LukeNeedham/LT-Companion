package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.ui.player.PlaybackRepository
import com.lukeneedham.languagetransfer.ui.player.PlayingState
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.EventChannel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

/**
 * ViewModel for the Lesson page.
 * This VM just handles delegating to the VM for the actual current lesson,
 * and for changing lessons.
 */
class LessonViewModel(
    private val initialLesson: CourseLesson,
    private val lessonSpecificViewModelFactory: LessonSpecificViewModelFactory,
    private val debugOptions: DebugOptions,
    private val playbackRepository: PlaybackRepository,
) : ViewModel() {

    private var specificVmCollectorsJob: Job? = null

    private val onBackMutable = EventChannel()
    val onBack = onBackMutable.flow

    var uiState by mutableStateOf<LessonState>(LessonState.Loading)
        private set

    var showDebugLessonControls by mutableStateOf(false)
        private set

    var lesson by mutableStateOf<CourseLesson>(initialLesson)
        private set

    val pausepointReporter = object : PausepointReporter {
        override fun add() {
            specificLessonViewModel.reportPausepointMissing()
        }

        override fun remove() {
            specificLessonViewModel.reportPausepointUnnecessary()
        }

        override fun shiftLater() {
            specificLessonViewModel.reportPausepointTooEarly()
        }

        override fun shiftEarlier() {
            specificLessonViewModel.reportPausepointTooLate()
        }
    }

    private var specificLessonViewModel = createLessonSpecificViewModel(initialLesson)

    init {
        viewModelScope.launch {
            debugOptions.showDebugLessonControls.collect {
                showDebugLessonControls = it
            }
        }

        viewModelScope.launch {
            playbackRepository.resumeChannel.collect {
                when (uiState) {
                    is LessonState.Completed -> {
                        continueToNextLesson()
                    }

                    is LessonState.Error,
                    is LessonState.InProgress,
                    LessonState.Loading -> {
                        // Nothing to do
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        specificLessonViewModel.dispose()
    }

    fun skipBackward() {
        specificLessonViewModel.skipBackward()
    }

    fun togglePlaybackSpeed() {
        specificLessonViewModel.togglePlaybackSpeed()
    }

    fun skipToEnd() {
        specificLessonViewModel.skipToEnd()
    }

    fun jumpForward() {
        specificLessonViewModel.jumpForward()
    }

    fun onMainButtonClick() {
        val state = uiState
        when (state) {
            is LessonState.Error,
            LessonState.Loading -> {
                // Ignore
            }

            is LessonState.InProgress -> {
                // Toggle playback
                when (state.playingState) {
                    is PlayingState.Playing -> specificLessonViewModel.pausePlayback()
                    is PlayingState.Paused -> specificLessonViewModel.resumePlayback()
                }
            }

            is LessonState.Completed -> {
                // Go to next lesson, or back to home if completed
                if (state.hasCompletedCourse) {
                    onBack()
                } else {
                    continueToNextLesson()
                }
            }
        }
    }

    fun onBack() {
        onBackMutable.send()
    }

    fun continueToNextLesson() {
        val next = specificLessonViewModel.nextLesson ?: return

        lesson = next
        // Dispose of the VM for the current lesson - perform clean-up
        specificLessonViewModel.dispose()
        specificLessonViewModel = createLessonSpecificViewModel(next)
    }

    private fun createLessonSpecificViewModel(lesson: CourseLesson): LessonSpecificViewModel {
        val childScopeName = "Lesson-specific scope for lesson ${lesson.lessonNumber}"

        /**
         * New child coroutine scope that gets ended when the lesson changes
         * and the lesson-specific VM gets disposed
         */
        val childScope = CoroutineScope(
            viewModelScope.coroutineContext +
                    // Need to ensure the child has a new job independent of the parent job
                    SupervisorJob(viewModelScope.coroutineContext.job) +
                    CoroutineName(childScopeName)
        )

        val vm = lessonSpecificViewModelFactory.create(lesson, childScope)

        specificVmCollectorsJob?.cancel()
        specificVmCollectorsJob = viewModelScope.launch {
            launch {
                vm.uiState.collect {
                    uiState = it
                }
            }
        }
        return vm
    }
}
