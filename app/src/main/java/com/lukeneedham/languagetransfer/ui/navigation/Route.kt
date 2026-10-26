package com.lukeneedham.languagetransfer.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import kotlinx.serialization.Serializable

/**
 * The navigation destinations of the app, one per page: [Home] is `HomePage`, and so on.
 */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Startup : Route

    @Serializable
    data object CourseDownload : Route

    /**
     * The home page where users can access the downloaded courses.
     */
    @Serializable
    data object Home : Route

    /**
     * The lesson page where users can play and interact with a specific lesson.
     *
     * @property lesson The audio lesson to play
     */
    @Serializable
    data class Lesson(val lesson: CourseLesson) : Route

    /**
     * The settings page. Holds the button that opens the debug page.
     */
    @Serializable
    data object Settings : Route

    /**
     * The app's own debug page, for navigating to any other page.
     */
    @Serializable
    data object AppDebug : Route

    /** The shared library's debug page: app info and animation speed. */
    @Serializable
    data object Debug : Route

    /** The shared library's log viewer. */
    @Serializable
    data object Logs : Route

    /** The shared library's crash viewer. */
    @Serializable
    data object Crashes : Route
}
