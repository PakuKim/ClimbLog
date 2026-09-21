package io.paku.climblog.presentation.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.paku.climblog.presentation.component.PreviewWrapper
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.navigation.MainBottomNavigation
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun MainRoute(
    viewModel: MainViewModel = koinViewModel(),
    mainBuilder: NavGraphBuilder.() -> Unit,
) {
    val state by viewModel.state

    MainScreen(
        state = state,
        mainBuilder = mainBuilder
    )
}


@Composable
private fun MainScreen(
    state: MainViewModelState,
    mainBuilder: NavGraphBuilder.() -> Unit,
) {
    val mainNavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // NavHost takes the full screen now
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = mainNavController,
            startDestination = AppNavigation.Home,
            builder = mainBuilder,
        )

        // Floating Bottom Navigation overlaid on top
        if (state.isBottomBarVisible) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                MainBottomNavigationScreen(
                    navController = mainNavController
                )
            }
        }
    }
}

@Composable
private fun MainBottomNavigationScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    Box(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            tonalElevation = 8.dp,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    MainBottomNavigation.Home,
                    MainBottomNavigation.Search,
                    MainBottomNavigation.Profile
                ).forEach { screen ->
                    val isSelected = navBackStackEntry?.destination?.hasRoute(screen.destination::class) ?: false
                    val scale by animateFloatAsState(if (isSelected) 1.2f else 1.0f)

                    Box(
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    navController.navigate(screen.destination) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = screen.selectedIcon,
                            contentDescription = screen.title,
                            modifier = Modifier
                                .size(26.dp)
                                .scale(scale),
                            tint = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    PreviewWrapper {
        MainScreen(
            state = MainViewModelState(),
            mainBuilder = {}
        )
    }
}
