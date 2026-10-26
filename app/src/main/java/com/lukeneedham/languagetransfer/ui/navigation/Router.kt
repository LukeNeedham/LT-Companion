package com.lukeneedham.languagetransfer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lukeneedham.common.ui.CrashesPage
import com.lukeneedham.common.ui.LogsPage
import com.lukeneedham.languagetransfer.ui.feature.debug.AppDebugPage
import com.lukeneedham.languagetransfer.ui.feature.settings.SettingsPage
import com.lukeneedham.languagetransfer.ui.feature.downloadlanguage.DownloadLanguagePage
import com.lukeneedham.languagetransfer.ui.feature.home.HomePage
import com.lukeneedham.languagetransfer.ui.feature.lesson.LessonPage
import com.lukeneedham.languagetransfer.ui.feature.startup.StartupPage
import com.lukeneedham.languagetransfer.ui.theme.Colors
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popAll
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun Router() {
    val navController = rememberNavController<Route>(
        startDestination = Route.Startup
    )

    val onBack: () -> Unit = {
        navController.pop()
    }

    NavBackHandler(navController)

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Default background for all pages. Avoids a flicker on first load
            .background(color = Colors.background)
    ) {
        AnimatedNavHost(
            controller = navController,
            transitionSpec = { action, from, to ->
                val none = EnterTransition.None togetherWith ExitTransition.None

                val slideDirection = if (action == NavAction.Pop) {
                    AnimatedContentTransitionScope.SlideDirection.Down
                } else {
                    AnimatedContentTransitionScope.SlideDirection.Up
                }
                val slide =
                    slideIntoContainer(slideDirection) togetherWith slideOutOfContainer(
                        slideDirection
                    )

                return@AnimatedNavHost when {
                    to is Route.Startup -> none
                    from is Route.Startup -> none
                    else -> slide
                }
            },
        ) { page ->
            when (page) {
                is Route.Startup -> {
                    StartupPage(
                        continueToHome = {
                            navController.popAll()
                            navController.navigate(Route.Home)
                        },
                        continueToCourseDownload = {
                            navController.popAll()
                            navController.navigate(Route.CourseDownload)
                        },
                    )
                }

                is Route.CourseDownload -> {
                    DownloadLanguagePage(
                        onContinue = {
                            navController.navigate(Route.Home)
                        }
                    )
                }

                is Route.Home -> {
                    HomePage(
                        onLessonClick = { lesson ->
                            navController.navigate(Route.Lesson(lesson))
                        },
                        onSettingsClick = {
                            navController.navigate(Route.Settings)
                        }
                    )
                }

                is Route.Lesson -> {
                    val lesson = page.lesson
                    LessonPage(
                        initialLesson = lesson,
                        goBack = onBack,
                    )
                }

                is Route.Settings -> {
                    SettingsPage(
                        onBack = onBack,
                        onOpenDebug = { navController.navigate(Route.AppDebug) },
                    )
                }

                is Route.Debug -> {
                    com.lukeneedham.common.ui.DebugPage(
                        onBack = onBack,
                        onOpenLogs = { navController.navigate(Route.Logs) },
                        onOpenCrashes = { navController.navigate(Route.Crashes) },
                    )
                }

                is Route.Logs -> LogsPage(onBack = onBack)

                is Route.Crashes -> CrashesPage(onBack = onBack)

                is Route.AppDebug -> {
                    AppDebugPage(
                        onBack = onBack,
                        openCommonDebug = { navController.navigate(Route.Debug) },
                        openStartup = { navController.navigate(Route.Startup) },
                        openCourseDownload = { navController.navigate(Route.CourseDownload) },
                        openHome = { navController.navigate(Route.Home) },
                        openLesson = { lesson -> navController.navigate(Route.Lesson(lesson)) }
                    )
                }
            }
        }
    }
}
