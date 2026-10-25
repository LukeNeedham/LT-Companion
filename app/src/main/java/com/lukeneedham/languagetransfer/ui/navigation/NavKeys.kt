package com.lukeneedham.languagetransfer.ui.navigation

import android.os.Parcelable
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing the different navigation destinations in the app.
 */
@Parcelize
sealed interface AppNavKey : Parcelable

object StartupNavKey : AppNavKey

object CourseDownloadNavKey : AppNavKey

/**
 * The home page where users can access the downloaded courses.
 */
object HomeNavKey : AppNavKey

/**
 * The lesson page where users can play and interact with a specific lesson.
 *
 * @property lesson The audio lesson to play
 */
data class LessonNavKey(val lesson: CourseLesson) : AppNavKey

/**
 * The settings page. Holds the button that opens the debug page.
 */
object SettingsNavKey : AppNavKey

/**
 * The app's own debug page, for navigating to any other page.
 */
object AppDebugNavKey : AppNavKey

/** The shared library's debug page: app info and animation speed. */
object DebugNavKey : AppNavKey

/** The shared library's log viewer. */
object LogsNavKey : AppNavKey

/** The shared library's crash viewer. */
object CrashesNavKey : AppNavKey
