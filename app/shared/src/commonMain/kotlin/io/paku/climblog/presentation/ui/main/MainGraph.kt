package io.paku.climblog.presentation.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.navigation.MainBottomNavigation
import io.paku.climblog.presentation.ui.main.home.HomeFeedScreen
import io.paku.climblog.presentation.ui.main.notification.NotificationScreen
import io.paku.climblog.presentation.ui.main.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.main.profile.ProfileScreen
import io.paku.climblog.presentation.ui.main.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.main.profile.UserProfileScreen
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileScreen
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileViewModel
import io.paku.climblog.presentation.ui.main.search.SearchScreen
import io.paku.climblog.presentation.ui.main.settings.SettingsScreen
import io.paku.climblog.presentation.ui.main.settings.SettingsViewModel
import io.paku.climblog.presentation.ui.main.upload.VideoUploadScreen
import io.paku.climblog.presentation.ui.main.upload.VideoUploadViewModel
import org.koin.compose.koinInject

internal fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    composable<AppNavigation.Main> {
        MainRoute(
            mainBuilder = {
                composable<MainBottomNavigation.Home> {
                    HomeFeedScreen()
                }

                composable<MainBottomNavigation.Search> {
                    SearchScreen(
                        onUserClick = { userId ->
                            navController.navigate(AppNavigation.UserProfile(userId))
                        },
                        onVideoClick = { videoId ->
//                            navController.navigate(AppNavigation.Video(videoId))
                        }
                    )
                }

                composable<MainBottomNavigation.Profile> {
                    ProfileScreen(
                        onUploadClick = { navController.navigate(AppNavigation.Upload) },
                        onVideoClick = { videoId ->
//                            navController.navigate(AppNavigation.Video(videoId))
                        },
                        onMenuClick = { navController.navigate(AppNavigation.Settings) },
                        onEditClick = { navController.navigate(AppNavigation.EditProfile) }
                    )
                }
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
        val args: AppNavigation.UserProfile = backStackEntry.toRoute()
        val profileViewModel: ProfileViewModel = koinInject()
        UserProfileScreen(
            userId = args.userId,
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
            }
        )
    }
}