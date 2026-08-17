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
import io.paku.climblog.presentation.ui.notification.NotificationScreen
import io.paku.climblog.presentation.ui.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.onboard.login.LoginScreen
import io.paku.climblog.presentation.ui.onboard.login.LoginViewModel
import io.paku.climblog.presentation.ui.onboard.register.RegisterScreen
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import io.paku.climblog.presentation.ui.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.profile.UserProfileScreen
import io.paku.climblog.presentation.ui.profile.edit.EditProfileScreen
import io.paku.climblog.presentation.ui.profile.edit.EditProfileViewModel
import io.paku.climblog.presentation.ui.settings.SettingsScreen
import io.paku.climblog.presentation.ui.settings.SettingsViewModel
import io.paku.climblog.presentation.ui.upload.VideoUploadScreen
import io.paku.climblog.presentation.ui.upload.VideoUploadViewModel
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
                    navController.navigate(AppNavigation.Login) {
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
                }

                composable<AppNavigation.Login> {
                    val loginViewModel: LoginViewModel = koinInject()
                    LoginScreen(
                        viewModel = loginViewModel,
                        onNavigateToMain = {
                            navController.navigate(AppNavigation.Main) {
                                popUpTo(AppNavigation.Login) { inclusive = true }
                            }
                        },
                        onNavigateToOnboard = { _, _ ->
                            navController.navigate(AppNavigation.Onboard) {
                                popUpTo(AppNavigation.Login) { inclusive = true }
                            }
                        }
                    )
                }

                composable<AppNavigation.Onboard> {
                    val registerViewModel: RegisterViewModel = koinInject()
                    RegisterScreen(viewModel = registerViewModel)
                }

                composable<AppNavigation.Main> {
                    MainScreen(
                        onUploadClick = { navController.navigate(AppNavigation.Upload) },
                        onNotificationClick = { navController.navigate(AppNavigation.Notifications) },
                        onVideoClick = { /* Handle video click */ },
                        onUserClick = { userId ->
                            navController.navigate(AppNavigation.UserProfile(userId))
                        },
                        onMenuClick = {
                            navController.navigate(AppNavigation.Settings)
                        },
                        onEditClick = {
                            navController.navigate(AppNavigation.EditProfile)
                        }
                    )
                }
                
                composable<AppNavigation.Notifications> {
                    val notificationViewModel: NotificationViewModel = koinInject()
                    NotificationScreen(
                        viewModel = notificationViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onUserClick = { _ ->
                        },
                        onVideoClick = { _ ->
                        }
                    )
                }
                
                composable<AppNavigation.Upload> {
                    val uploadViewModel: VideoUploadViewModel = koinInject()
                    VideoUploadScreen(
                        viewModel = uploadViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onUploadSuccess = {
                            navController.navigate(AppNavigation.Main) {
                                popUpTo(AppNavigation.Main) { inclusive = true }
                            }
                        }
                    )
                }

                composable<AppNavigation.UserProfile> { backStackEntry ->
                    val userId = 1L // Extracting args from backStackEntry in real app
                    val profileViewModel: ProfileViewModel = koinInject()
                    UserProfileScreen(
                        userId = userId,
                        viewModel = profileViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onVideoClick = { }
                    )
                }

                composable<AppNavigation.EditProfile> {
                    val editProfileViewModel: EditProfileViewModel = koinInject()
                    EditProfileScreen(
                        viewModel = editProfileViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable<AppNavigation.Settings> {
                    val settingsViewModel: SettingsViewModel = koinInject()
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onLogoutSuccess = {
                            navController.navigate(AppNavigation.Login) {
                                popUpTo(AppNavigation.Main) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
