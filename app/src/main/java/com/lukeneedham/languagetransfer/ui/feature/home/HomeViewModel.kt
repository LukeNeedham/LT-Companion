package com.lukeneedham.languagetransfer.ui.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.data.repository.CompletedLessonRepository
import com.lukeneedham.languagetransfer.ui.feature.home.model.HomeLessonItem
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.DebugOptions
import com.lukeneedham.languagetransfer.util.EventDataChannel
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen that handles loading audio lessons data
 * and exposing UI state.
 */
class HomeViewModel(
    private val audioLessonRepository: AudioLessonRepository,
    private val completedLessonRepository: CompletedLessonRepository,
    private val debugOptions: DebugOptions,
) : ViewModel() {
    private var completedLessonNumbers: List<Int>? = null
    private var mostRecentCompletedLessonNumber: Int? = null
    private var debugAllLessonsCompleted: Boolean = false
    private val courseResult = audioLessonRepository.getLanguageCourse()

    var uiState by mutableStateOf<HomeState>(HomeState.Loading)
        private set

    private val _scrollToLessonFlow = EventDataChannel<Int>()
    val scrollToCurrentLessonFlow = _scrollToLessonFlow.asSharedFlow()

    init {
        observeCompletedLessons()
        observeMostRecentCompletedLesson()
        viewModelScope.launch {
            debugOptions.allLessonsCompleted.collect {
                debugAllLessonsCompleted = it
                refreshUiState()
            }
        }
    }

    private fun refreshUiState() {
        val oldState = uiState
        val newState = calculateUiState()
        uiState = newState

        if (newState is HomeState.Success && oldState !is HomeState.Success) {
            val currentLessonIndex =
                newState.lessons.indexOfFirst { it.progress == HomeLessonItem.Progress.Current }
            val scrollTo = if (currentLessonIndex == -1) 0 else currentLessonIndex
            _scrollToLessonFlow.tryEmit(scrollTo)
        }
    }

    private fun calculateUiState(): HomeState {
        val courseRes = courseResult

        val completed = completedLessonNumbers ?: return HomeState.Loading

        return when (courseRes) {
            is AppResult.Failure -> HomeState.Error(
                courseRes.error.message ?: "Unknown error"
            )

            is AppResult.Success -> {
                val course = courseRes.value

                val lastCompletedNum = mostRecentCompletedLessonNumber
                val lessonNumWithBookmark = if (lastCompletedNum == null) null else lastCompletedNum + 1

                val lessons = course.lessons.map { lesson ->
                    val lessonNumber = lesson.lessonNumber
                    val previousLessonNumber = lessonNumber - 1
                    val progress = when {
                        debugAllLessonsCompleted -> HomeLessonItem.Progress.Completed

                        lessonNumber in completed ->
                            HomeLessonItem.Progress.Completed

                        previousLessonNumber in completed ||
                                // When the current lesson is the first lesson
                                // there won't be any completed lessons
                                previousLessonNumber == 0 -> HomeLessonItem.Progress.Current

                        else -> HomeLessonItem.Progress.Locked
                    }

                    /** This item has a bookmark when the previous lesson is the last completed lesson */
                    val hasBookmark = lessonNumber == lessonNumWithBookmark
                    HomeLessonItem(
                        lesson = lesson,
                        progress = progress,
                        hasBookmark = hasBookmark,
                    )
                }

                HomeState.Success(lessons)
            }
        }
    }

    private fun observeCompletedLessons() {
        viewModelScope.launch {
            completedLessonRepository.getAllCompletedLessonsFlow().collect {
                completedLessonNumbers = it.map { it.lessonId }
                refreshUiState()
            }
        }
    }

    fun observeMostRecentCompletedLesson() {
        viewModelScope.launch {
            completedLessonRepository.getMostRecentCompletedLessonFlow().collect {
                mostRecentCompletedLessonNumber = it.lessonId
                refreshUiState()
            }
        }
    }
}
