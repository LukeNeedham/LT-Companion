package com.lukeneedham.languagetransfer.ui.feature.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.languagetransfer.data.repository.AudioLessonRepository
import com.lukeneedham.languagetransfer.domain.pausepointreport.LessonPausepointProvider
import com.lukeneedham.languagetransfer.util.AppResult
import com.lukeneedham.languagetransfer.util.DebugOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DebugViewModel(
    private val debugOptions: DebugOptions,
    private val audioLessonRepository: AudioLessonRepository,
    private val lessonPausepointProviderFactory: LessonPausepointProvider.Factory,
) : ViewModel() {
    val allLessonsCompleted = debugOptions.allLessonsCompleted
    val showDebugLessonControls = debugOptions.showDebugLessonControls
    val shouldAutoPause = debugOptions.shouldAutoPause

    var modifiedPausepointsJson by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            val flow = getModifiedPausepointsJsonFlow()
            if (flow == null) {
                modifiedPausepointsJson = ""
            } else {
                flow.collect {
                    modifiedPausepointsJson = it
                }
            }
        }
    }

    fun setAllLessonsCompleted(value: Boolean) {
        debugOptions.setAllLessonsCompleted(value)
    }

    fun setShowDebugLessonControls(value: Boolean) {
        debugOptions.setShowDebugLessonControls(value)
    }

    fun setShouldAutoPause(value: Boolean) {
        debugOptions.setShouldAutoPause(value)
    }

    private fun getModifiedPausepointsJsonFlow(): Flow<String>? {
        val course = audioLessonRepository.getLanguageCourse()
        return when (course) {
            is AppResult.Failure -> null
            is AppResult.Success -> {
                val lessons = course.value.lessons
                val pausepointFlows = lessons.map { lesson ->
                    val pausepointProvider = lessonPausepointProviderFactory.build(lesson)
                    pausepointProvider.pausepoints.map {
                        lesson.name to it
                    }
                }
                combine(pausepointFlows) { values ->
                    val fields = values.map { (lessonName, pausepoints) ->
                        "    \"${lessonName}\": $pausepoints"
                    }.joinToString(",\n")
                    "{\n$fields\n}"
                }
            }
        }
    }
} 