package io.paku.climblog.presentation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.paku.climblog.di.appModule
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.theme.AppTheme
import io.paku.climblog.presentation.ui.main.MainScreen
import io.paku.climblog.presentation.ui.onboard.register.RegisterScreen
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
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
            val viewModel: AppViewModel = koinInject()
            val navController = rememberNavController()

            LaunchedEffect(viewModel.authorized.value) {
                if (viewModel.authorized.value) {
                    navController.navigate(AppNavigation.Main) {
                        popUpTo(AppNavigation.Splash) { inclusive = true }
                    }
                } else {
                    navController.navigate(AppNavigation.Onboard) {
                        popUpTo(AppNavigation.Splash) { inclusive = true }
                    }
                }
            }

            NavHost(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
                startDestination = AppNavigation.Splash,
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
                composable<AppNavigation.Splash> {
                    // Splash UI
                }

                composable<AppNavigation.Onboard> {
                    val registerViewModel: RegisterViewModel = koinInject()
                    RegisterScreen(viewModel = registerViewModel)
                }

                composable<AppNavigation.Main> {
                    MainScreen(
                        onUploadClick = { navController.navigate(AppNavigation.Upload) },
                        onNotificationClick = { navController.navigate(AppNavigation.Notifications) },
                        onVideoClick = { /* Handle video click */ }
                    )
                }
                
                composable<AppNavigation.Notifications> {
                    // NotificationListScreen()
                }
                
                composable<AppNavigation.Upload> {
                    // VideoUploadScreen()
                }
            }
        }
    }
}
