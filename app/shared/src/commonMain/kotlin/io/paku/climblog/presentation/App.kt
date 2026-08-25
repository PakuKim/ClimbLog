package io.paku.climblog.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paku.climblog.di.appModule
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.theme.AppTheme
import io.paku.climblog.presentation.ui.main.mainGraph
import io.paku.climblog.presentation.ui.onboard.onboardGraph
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = KoinConfiguration(
            config = {
                modules(appModule())
            }
        )
    ) {
        AppTheme {
            val viewModel: AppViewModel = koinViewModel()
            val navController = rememberNavController()
            var startDestination: AppNavigation by remember { mutableStateOf(AppNavigation.Splash) }

            LaunchedEffect(viewModel.authorized.value) {
                startDestination = if (viewModel.authorized.value) AppNavigation.Main else AppNavigation.Login
            }

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = AppNavigation.Login,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<AppNavigation.Splash> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {

                    }
                }

                onboardGraph(navController)

                mainGraph(navController)
            }
        }
    }
}
