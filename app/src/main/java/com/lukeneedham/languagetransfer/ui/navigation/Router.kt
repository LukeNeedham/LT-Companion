package com.lukeneedham.languagetransfer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.lukeneedham.common.ui.CrashesPage
import com.lukeneedham.common.ui.DebugPage
import com.lukeneedham.common.ui.LogsPage
import com.lukeneedham.languagetransfer.ui.feature.debug.AppDebugPage
import com.lukeneedham.languagetransfer.ui.feature.downloadlanguage.DownloadLanguagePage
import com.lukeneedham.languagetransfer.ui.feature.home.HomePage
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonPage
import com.lukeneedham.languagetransfer.ui.feature.settings.SettingsPage
import com.lukeneedham.languagetransfer.ui.feature.startup.StartupPage

/** Calls the page of [route], with the navigation as lambdas. The only place that calls a page. */
@Composable
fun Router(backStack: NavBackStack<NavKey>, route: Route) {
    // The common actions are defined once here, never repeated in each branch
    fun navigateTo(target: Route) { backStack.add(target) }
    fun navigateToAndClear(target: Route) {
        backStack.clear()
        backStack.add(target)
    }
    fun onBack() { backStack.removeLastOrNull() }

    when (route) { // exhaustive: no `else`
        Route.Startup -> StartupPage(
            continueToHome = { navigateToAndClear(Route.Home) },
            continueToCourseDownload = { navigateToAndClear(Route.CourseDownload) },
        )

        Route.CourseDownload -> DownloadLanguagePage(
            onContinue = { navigateTo(Route.Home) },
        )

        Route.Home -> HomePage(
            onLessonClick = { lesson -> navigateTo(Route.Lesson(lesson)) },
            onSettingsClick = { navigateTo(Route.Settings) },
        )

        is Route.Lesson -> LessonPage(
            initialLesson = route.lesson,
            goBack = ::onBack,
        )

        Route.Settings -> SettingsPage(
            onBack = ::onBack,
            onOpenDebug = { navigateTo(Route.AppDebug) },
        )

        Route.Debug -> DebugPage(
            onBack = ::onBack,
            onOpenLogs = { navigateTo(Route.Logs) },
            onOpenCrashes = { navigateTo(Route.Crashes) },
        )

        Route.Logs -> LogsPage(onBack = ::onBack)

        Route.Crashes -> CrashesPage(onBack = ::onBack)

        Route.AppDebug -> AppDebugPage(
            onBack = ::onBack,
            openCommonDebug = { navigateTo(Route.Debug) },
            openStartup = { navigateTo(Route.Startup) },
            openCourseDownload = { navigateTo(Route.CourseDownload) },
            openHome = { navigateTo(Route.Home) },
            openLesson = { lesson -> navigateTo(Route.Lesson(lesson)) },
        )
    }
}
