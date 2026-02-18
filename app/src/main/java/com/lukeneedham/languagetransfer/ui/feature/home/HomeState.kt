package com.lukeneedham.languagetransfer.ui.feature.home

import com.lukeneedham.languagetransfer.ui.feature.home.model.HomeLessonItem

/**
 * Represents the current state of the Home screen UI.
 */
sealed class HomeState {
    /**
     * Initial loading state.
     */
    object Loading : HomeState()

    /**
     * Success state with loaded language course data.
     */
    data class Success(
        val lessons: List<HomeLessonItem>,
    ) : HomeState()

    /**
     * Error state with error message.
     */
    data class Error(val message: String) : HomeState()
}
