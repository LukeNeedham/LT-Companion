package com.lukeneedham.languagetransfer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import com.lukeneedham.languagetransfer.ui.theme.Colors

/** Owns the back stack and the [NavDisplay]. Called only by `Root`. */
@Composable
fun AppNavigation() {
    val backStack = rememberNavBackStack(Route.Startup)

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Default background for all pages. Avoids a flicker on first load
            .background(color = Colors.background)
    ) {
        NavDisplay(
            backStack = backStack,
            // Each page has a view model of its own, kept for as long as the page is on the back stack
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = { slide(AnimatedContentTransitionScope.SlideDirection.Up) },
            popTransitionSpec = { slide(AnimatedContentTransitionScope.SlideDirection.Down) },
            predictivePopTransitionSpec = { slide(AnimatedContentTransitionScope.SlideDirection.Down) },
            entryProvider = { key -> routeEntryProvider(backStack, key) },
        )
    }
}

/** Pages slide over one another, except into and out of the startup page. */
private fun AnimatedContentTransitionScope<Scene<NavKey>>.slide(
    direction: AnimatedContentTransitionScope.SlideDirection,
): ContentTransform {
    val isStartup = initialState.isStartup() || targetState.isStartup()
    return if (isStartup) {
        EnterTransition.None togetherWith ExitTransition.None
    } else {
        slideIntoContainer(direction) togetherWith slideOutOfContainer(direction)
    }
}

private fun Scene<NavKey>.isStartup() = entries.lastOrNull()?.key is Route.Startup

private fun routeEntryProvider(backStack: NavBackStack<NavKey>, key: NavKey): NavEntry<NavKey> {
    val route = key as Route
    return NavEntry(route) {
        Router(backStack, route)
    }
}
