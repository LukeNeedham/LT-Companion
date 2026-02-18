package com.lukeneedham.languagetransfer.ui.navigation

import android.os.Parcelable
import com.lukeneedham.languagetransfer.domain.model.CourseLesson
import kotlinx.parcelize.Parcelize

/**
 * Sealed class representing the different navigation destinations in the app.
 */
@Parcelize
sealed interface Page : Parcelable {

    object Startup : Page

    object CourseDownload : Page

    /**
     * The home page where users can access the downloaded courses.
     */
    object Home : Page

    /**
     * The lesson page where users can play and interact with a specific lesson.
     *
     * @property lesson The audio lesson to play
     */
    data class Lesson(val lesson: CourseLesson) : Page

    /**
     * The debug page for navigating to any other page.
     */
    object Debug : Page

    enum class Animation {
        None,
        Default,
    }
}
