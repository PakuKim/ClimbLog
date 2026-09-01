package io.paku.climblog.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.paku.climblog.presentation.navigation.MainBottomNavigation
import io.paku.climblog.presentation.theme.AppComponentColors
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

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            MainBottomNavigationScreen(
                navController = mainNavController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize(),
                navController = mainNavController,
                startDestination = MainBottomNavigation.Home.route,
                builder = mainBuilder,
            )
        }
    }
}

@Composable
private fun MainBottomNavigationScreen(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        listOf(
            MainBottomNavigation.Home,
            MainBottomNavigation.Search,
            MainBottomNavigation.Profile
        ).forEach { screen ->
            NavigationBarItem(
                colors = AppComponentColors.navigationBarColors(),
                selected = screen.route == currentRoute,
                icon = {
                    Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(text = screen.title)
                },
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
