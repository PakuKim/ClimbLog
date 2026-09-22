package io.paku.climblog.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainBottomNavigation(
    val destination: Any,
    val title: String,
    val selectedIcon: ImageVector
) {
    data object Home: MainBottomNavigation(
        destination = AppNavigation.Home,
        title = "홈",
        selectedIcon = Icons.Default.Home
    )
    data object Search: MainBottomNavigation(
        destination = AppNavigation.Search,
        title = "검색",
        selectedIcon = Icons.Default.Search
    )
    data object Profile: MainBottomNavigation(
        destination = AppNavigation.Profile,
        title = "프로필",
        selectedIcon = Icons.Default.Person
    )
}