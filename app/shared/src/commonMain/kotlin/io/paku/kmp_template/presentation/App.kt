package io.paku.kmp_template.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paku.kmp_template.di.appModule
import io.paku.kmp_template.presentation.navigation.AppNavigation
import io.paku.kmp_template.presentation.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.dsl.KoinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = KoinConfiguration(
            config = {
                modules(appModule())
            }
        )
    ) {
        AppTheme {
            val viewModel: AppViewModel = koinInject()
            val navController = rememberNavController()

            LaunchedEffect(viewModel.authorized) {
                if (viewModel.authorized.value) {
                    navController.navigate(AppNavigation.Main)
                } else {
                    navController.navigate(AppNavigation.Onboard)
                }
            }

            NavHost(
                modifier = Modifier
                    .fillMaxSize(),
                navController = navController,
                startDestination = AppNavigation.Splash,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable<AppNavigation.Splash> {

                }

                composable<AppNavigation.Onboard> {

                }

                composable<AppNavigation.Main> {

                }
            }
        }
    }
}