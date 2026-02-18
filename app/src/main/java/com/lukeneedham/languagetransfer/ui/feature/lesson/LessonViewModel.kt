package com.lukeneedham.languagetransfer.ui.feature.lesson

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import com.lukeneedham.languagetransfer.ui.feature.lesson.pausepointreport.PausepointReporter
import com.lukeneedham.languagetransfer.util.EventChannel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * ViewModel for the Lesson page.
 * This VM just handles delegating to the VM for the actual current lesson,
 * and for changing lessons.
 */
class LessonViewModel(
    private val initialLesson: CourseLesson,
    private val lessonSpecificViewModelFactory: LessonSpecificViewModelFactory,
) : ViewModel() {

    private val onBackMutable = EventChannel()
    val onBack = onBackMutable.flow

    var uiState by mutableStateOf<LessonState>(LessonState.Loading)
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
        specificLessonViewModel.onMainButtonClick()
    }

    fun onBack() {
        onBackMutable.send()
    }

    fun continueToNextLesson() {
        val next = specificLessonViewModel.nextLesson ?: return

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
        val childScope =
            CoroutineScope(viewModelScope.coroutineContext + CoroutineName(childScopeName))

        val vm = lessonSpecificViewModelFactory.create(lesson, childScope)
        childScope.launch {
            vm.onBack.collect {
                onBack()
            }
        }
        childScope.launch {
            vm.continueToNextLesson.collect {
                continueToNextLesson()
            }
        }
        childScope.launch {
            vm.uiState.collect {
                uiState = it
            }
        }
        return vm
    }
}
