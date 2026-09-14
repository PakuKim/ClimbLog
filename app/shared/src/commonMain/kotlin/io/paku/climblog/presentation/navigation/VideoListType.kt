package io.paku.climblog.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface VideoListType {
    @Serializable
    data object Home : VideoListType

    @Serializable
    data object My : VideoListType

    @Serializable
    data class User(val userId: Long) : VideoListType

    @Serializable
    data class Search(val query: String) : VideoListType

    @Serializable
    data class Single(val videoId: Long) : VideoListType
}