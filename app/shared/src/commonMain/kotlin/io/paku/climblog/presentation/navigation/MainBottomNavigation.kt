package io.paku.climblog.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainBottomNavigation(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector
) {
    data object Home: MainBottomNavigation(
        route = "Home",
        title = "홈",
        selectedIcon = Icons.Default.Home
    )
    data object Search: MainBottomNavigation(
        route = "Search",
        title = "검색",
        selectedIcon = Icons.Default.Search
    )
    data object Profile: MainBottomNavigation(
        route = "Profile",
        title = "프로필",
        selectedIcon = Icons.Default.Person
    )
}