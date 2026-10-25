package com.lukeneedham.languagetransfer.ui.navigation

import android.os.Parcelable
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import kotlinx.parcelize.Parcelize

/**
 * The navigation destinations of the app, one per page: [Home] is `HomePage`, and so on.
 */
@Parcelize
sealed interface Route : Parcelable {
    object Startup : Route

    object CourseDownload : Route

    /**
     * The home page where users can access the downloaded courses.
     */
    object Home : Route

    /**
     * The lesson page where users can play and interact with a specific lesson.
     *
     * @property lesson The audio lesson to play
     */
    data class Lesson(val lesson: CourseLesson) : Route

    /**
     * The settings page. Holds the button that opens the debug page.
     */
    object Settings : Route

    /**
     * The app's own debug page, for navigating to any other page.
     */
    object AppDebug : Route

    /** The shared library's debug page: app info and animation speed. */
    object Debug : Route

    /** The shared library's log viewer. */
    object Logs : Route

    /** The shared library's crash viewer. */
    object Crashes : Route
}
