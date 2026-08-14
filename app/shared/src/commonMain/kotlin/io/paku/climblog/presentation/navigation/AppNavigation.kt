package io.paku.climblog.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavigation {
    @Serializable
    data object Splash : AppNavigation

    @Serializable
    data object Main : AppNavigation

    @Serializable
    data object Onboard : AppNavigation

    @Serializable
    data object Notifications : AppNavigation

    @Serializable
    data object Upload : AppNavigation
}
