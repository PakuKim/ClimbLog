package io.paku.climblog.presentation.ui.main

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.navigation.FollowListType
import io.paku.climblog.presentation.navigation.VideoListType
import io.paku.climblog.presentation.navigation.VideoListTypeNavType
import io.paku.climblog.presentation.ui.main.follow.FollowListRoute
import io.paku.climblog.presentation.ui.main.notification.NotificationRoute
import io.paku.climblog.presentation.ui.main.profile.ProfileRoute
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileRoute
import io.paku.climblog.presentation.ui.main.search.SearchRoute
import io.paku.climblog.presentation.ui.main.settings.SettingsRoute
import io.paku.climblog.presentation.ui.main.upload.VideoUploadRoute
import io.paku.climblog.presentation.ui.main.video.VideoRoute
import org.koin.compose.viewmodel.koinViewModel
import kotlin.reflect.typeOf

internal fun NavGraphBuilder.mainGraph(
    navController: NavController
) {
    composable<AppNavigation.Main> {
        val mainViewModel: MainViewModel = koinViewModel()
        MainRoute(
            viewModel = mainViewModel,
            mainBuilder = {
                composable<AppNavigation.Home> { backStackEntry ->
                    val homeArgs = backStackEntry.toRoute<AppNavigation.Home>()
                    VideoRoute(
                        videoListType = VideoListType.Home,
                        initialVideoId = homeArgs.initialVideoId,
                        isNavigationVisible = false,
                        onToggleBottomNavClick = {
                            mainViewModel.onEvent(MainViewModelEvent.SetBottomBarVisibility(it))
                        }
                    )
                }

                composable<AppNavigation.Search> {
                    SearchRoute(
                        onUserClick = { userId ->
                            navController.navigate(AppNavigation.UserProfile(userId))
                        },
                        onVideoClick = { videoId ->
                            navController.navigate(
                                AppNavigation.VideoFeed(
                                    type = VideoListType.Home,
                                    initialVideoId = videoId
                                )
                            )
                        }
                    )
                }

                composable<AppNavigation.Profile> {
                    ProfileRoute(
                        onUploadClick = { navController.navigate(AppNavigation.Upload) },
                        onVideoClick = { videoId ->
                            navController.navigate(
                                AppNavigation.VideoFeed(
                                    type = VideoListType.User(0),
                                    initialVideoId = videoId
                                )
                            )
                        },
                        onMenuClick = { navController.navigate(AppNavigation.Settings) },
                        onEditClick = { navController.navigate(AppNavigation.EditProfile) },
                        onFollowersClick = { userId ->
                            navController.navigate(AppNavigation.FollowList(userId, FollowListType.FOLLOWERS))
                        },
                        onFollowingClick = { userId ->
                            navController.navigate(AppNavigation.FollowList(userId, FollowListType.FOLLOWING))
                        }
                    )
                }
            }
        )
    }

    composable<AppNavigation.VideoFeed>(
        typeMap = mapOf(
            typeOf<VideoListType>() to VideoListTypeNavType
        )
    ) { backStackEntry ->
        val videoFeed = backStackEntry.toRoute<AppNavigation.VideoFeed>()
        VideoRoute(
            videoListType = videoFeed.type,
            initialVideoId = videoFeed.initialVideoId,
            onBackClick = { navController.popBackStack() },
            onUserClick = { userId ->
                navController.navigate(AppNavigation.UserProfile(userId))
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

    composable<AppNavigation.UserProfile> { backStackEntry ->
        val userProfile = backStackEntry.toRoute<AppNavigation.UserProfile>()
        ProfileRoute(
            onUploadClick = {},
            onVideoClick = { videoId ->
                navController.navigate(
                    AppNavigation.VideoFeed(
                        type = VideoListType.User(userProfile.userId ?: 0),
                        initialVideoId = videoId
                    )
                )
            },
            onMenuClick = {},
            onEditClick = {},
            onFollowersClick = { userId ->
                navController.navigate(AppNavigation.FollowList(userId, FollowListType.FOLLOWERS))
            },
            onFollowingClick = { userId ->
                navController.navigate(AppNavigation.FollowList(userId, FollowListType.FOLLOWING))
            }
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

    composable<AppNavigation.FollowList> {
        FollowListRoute(
            onUserClick = { userId ->
                navController.navigate(AppNavigation.UserProfile(userId))
            },
            onBackClick = { navController.popBackStack() }
        )
    }
}
