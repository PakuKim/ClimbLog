package io.paku.climblog.presentation.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import io.paku.climblog.business.domain.model.social.SocialLoginType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    data object Home : AppNavigation

    @Serializable
    data object Search : AppNavigation

    @Serializable
    data object Profile : AppNavigation

    @Serializable
    data object Notifications : AppNavigation

    @Serializable
    data object Upload : AppNavigation

    @Serializable
    data class UserProfile(
        val userId: Long? = null
    ) : AppNavigation

    @Serializable
    data object EditProfile : AppNavigation

    @Serializable
    data object Settings : AppNavigation

    @Serializable
    data class FollowList(
        val userId: Long,
        val type: FollowListType
    ) : AppNavigation

    @Serializable
    data class VideoFeed(
        val type: VideoListType = VideoListType.Home,
        val initialVideoId: Long? = null
    ) : AppNavigation
}

@Serializable
enum class FollowListType {
    FOLLOWERS, FOLLOWING
}

fun <T : Any> serializableNavType(
    serializer: KSerializer<T>,
    isNullableAllowed: Boolean = false
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: SavedState, key: String): T {
        return bundle.read { getString(key) }.let { Json.decodeFromString(serializer, it) }
    }

    override fun parseValue(value: String): T {
        return Json.decodeFromString(serializer, value)
    }

    override fun put(bundle: SavedState, key: String, value: T) {
        bundle.write { putString(key, Json.encodeToString(serializer, value)) }
    }

    override fun serializeAsValue(value: T): String {
        return Json.encodeToString(serializer, value)
    }
}

val VideoListTypeNavType = serializableNavType(VideoListType.serializer())
