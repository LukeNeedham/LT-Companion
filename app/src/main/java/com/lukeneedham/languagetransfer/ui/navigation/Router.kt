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
import com.lukeneedham.languagetransfer.ui.feature.debug.DebugPage
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
import dev.olshevski.navigation.reimagined.popUpTo
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun Router() {
    val navController = rememberNavController<Page>(
        startDestination = Page.Startup
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
                    to is Page.Startup -> none
                    from is Page.Startup -> none
                    else -> slide
                }
            },
        ) { page ->
            when (page) {
                is Page.Startup -> {
                    StartupPage(
                        continueToHome = {
                            navController.popAll()
                            navController.navigate(Page.Home)
                        },
                        continueToCourseDownload = {
                            navController.popAll()
                            navController.navigate(Page.CourseDownload)
                        },
                    )
                }

                is Page.CourseDownload -> {
                    DownloadLanguagePage(
                        onContinue = {
                            navController.navigate(Page.Home)
                        }
                    )
                }

                is Page.Home -> {
                    HomePage(
                        onLessonClick = { lesson ->
                            navController.navigate(Page.Lesson(lesson))
                        },
                        onDebugClick = {
                            navController.navigate(Page.Debug)
                        }
                    )
                }

                is Page.Lesson -> {
                    val lesson = page.lesson
                    LessonPage(
                        lesson = lesson,
                        goBack = onBack,
                    )
                }

                is Page.Debug -> {
                    DebugPage(
                        onBack = onBack,
                        openStartup = { navController.navigate(Page.Startup) },
                        openCourseDownload = { navController.navigate(Page.CourseDownload) },
                        openHome = { navController.navigate(Page.Home) },
                        openLesson = { lesson -> navController.navigate(Page.Lesson(lesson)) }
                    )
                }
            }
        }
    }
}
