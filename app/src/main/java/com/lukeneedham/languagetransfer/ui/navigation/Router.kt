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
    val navController = rememberNavController<AppNavKey>(
        startDestination = StartupNavKey
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
                    to is StartupNavKey -> none
                    from is StartupNavKey -> none
                    else -> slide
                }
            },
        ) { page ->
            when (page) {
                is StartupNavKey -> {
                    StartupPage(
                        continueToHome = {
                            navController.popAll()
                            navController.navigate(HomeNavKey)
                        },
                        continueToCourseDownload = {
                            navController.popAll()
                            navController.navigate(CourseDownloadNavKey)
                        },
                    )
                }

                is CourseDownloadNavKey -> {
                    DownloadLanguagePage(
                        onContinue = {
                            navController.navigate(HomeNavKey)
                        }
                    )
                }

                is HomeNavKey -> {
                    HomePage(
                        onLessonClick = { lesson ->
                            navController.navigate(LessonNavKey(lesson))
                        },
                        onSettingsClick = {
                            navController.navigate(SettingsNavKey)
                        }
                    )
                }

                is LessonNavKey -> {
                    val lesson = page.lesson
                    LessonPage(
                        initialLesson = lesson,
                        goBack = onBack,
                    )
                }

                is SettingsNavKey -> {
                    SettingsPage(
                        onBack = onBack,
                        onOpenDebug = { navController.navigate(AppDebugNavKey) },
                    )
                }

                is DebugNavKey -> {
                    com.lukeneedham.common.ui.DebugPage(
                        onBack = onBack,
                        onOpenLogs = { navController.navigate(LogsNavKey) },
                        onOpenCrashes = { navController.navigate(CrashesNavKey) },
                    )
                }

                is LogsNavKey -> LogsPage(onBack = onBack)

                is CrashesNavKey -> CrashesPage(onBack = onBack)

                is AppDebugNavKey -> {
                    AppDebugPage(
                        onBack = onBack,
                        openCommonDebug = { navController.navigate(DebugNavKey) },
                        openStartup = { navController.navigate(StartupNavKey) },
                        openCourseDownload = { navController.navigate(CourseDownloadNavKey) },
                        openHome = { navController.navigate(HomeNavKey) },
                        openLesson = { lesson -> navController.navigate(LessonNavKey(lesson)) }
                    )
                }
            }
        }
    }
}
