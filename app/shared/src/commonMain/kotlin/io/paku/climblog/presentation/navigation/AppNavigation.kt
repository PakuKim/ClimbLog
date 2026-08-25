package io.paku.climblog.presentation.navigation

import io.paku.climblog.business.domain.model.SocialLoginType
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavigation {
    @Serializable
    data object Splash: AppNavigation

    @Serializable
    data object Login : AppNavigation

    @Serializable
    data class Register(
        val socialLoginType: SocialLoginType
    ) : AppNavigation

    @Serializable
    data object Main : AppNavigation

    @Serializable
    data object Notifications : AppNavigation

    @Serializable
    data object Upload : AppNavigation

    @Serializable
    data class UserProfile(val userId: Long) : AppNavigation

    @Serializable
    data object EditProfile : AppNavigation

    @Serializable
    data object Settings : AppNavigation
}
