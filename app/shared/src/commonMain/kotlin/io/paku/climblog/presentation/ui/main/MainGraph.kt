package io.paku.climblog.presentation.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.navigation.MainBottomNavigation
import io.paku.climblog.presentation.ui.main.home.HomeRoute
import io.paku.climblog.presentation.ui.main.notification.NotificationRoute
import io.paku.climblog.presentation.ui.main.profile.ProfileRoute
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileRoute
import io.paku.climblog.presentation.ui.main.search.SearchRoute
import io.paku.climblog.presentation.ui.main.settings.SettingsRoute
import io.paku.climblog.presentation.ui.main.upload.VideoUploadRoute

internal fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    composable<AppNavigation.Main> {
        MainRoute(
            mainBuilder = {
                composable(
                    route = MainBottomNavigation.Home.route,
                ) {
                    HomeRoute(
                        navigateToUpload = {
                            navController.navigate(AppNavigation.Upload)
                        }
                    )
                }

                composable(
                    route = MainBottomNavigation.Search.route
                ) {
                    SearchRoute(
                        onUserClick = { userId ->
                            navController.navigate(AppNavigation.UserProfile(userId))
                        },
                        onVideoClick = { videoId ->
                            // navController.navigate(AppNavigation.VideoDetail(videoId))
                        }
                    )
                }

                composable(
                    route = MainBottomNavigation.Profile.route
                ) {
                    ProfileRoute(
                        onUploadClick = { navController.navigate(AppNavigation.Upload) },
                        onVideoClick = { videoId ->
                            // navController.navigate(AppNavigation.VideoDetail(videoId))
                        },
                        onMenuClick = { navController.navigate(AppNavigation.Settings) },
                        onEditClick = { navController.navigate(AppNavigation.EditProfile) }
                    )
                }
            }
        )
    }

    composable<AppNavigation.Notifications> {
        NotificationRoute(
            onNavigateBack = { navController.popBackStack() },
            onUserClick = { userId ->
                navController.navigate(AppNavigation.UserProfile(userId))
            },
            onVideoClick = { videoId ->
                // navController.navigate(AppNavigation.VideoDetail(videoId))
            }
        )
    }

    composable<AppNavigation.Upload> {
        VideoUploadRoute(
            onNavigateBack = { navController.popBackStack() },
            onUploadSuccess = {
                navController.popBackStack()
            }
        )
    }

    composable<AppNavigation.UserProfile> {
        ProfileRoute(
            onUploadClick = {},
            onVideoClick = { videoId ->
                // navController.navigate(AppNavigation.VideoDetail(videoId))
            },
            onMenuClick = {},
            onEditClick = {}
        )
    }

    composable<AppNavigation.EditProfile> {
        EditProfileRoute(
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable<AppNavigation.Settings> {
        SettingsRoute(
            onNavigateBack = { navController.popBackStack() },
            onLogoutSuccess = {
                // Handle logout navigation if needed, usually AppViewModel handles this via authorized state
            }
        )
    }
}
