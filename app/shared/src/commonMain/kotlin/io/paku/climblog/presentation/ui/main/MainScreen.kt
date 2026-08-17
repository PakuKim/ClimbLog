package io.paku.climblog.presentation.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.ui.home.HomeFeedScreen
import io.paku.climblog.presentation.ui.home.HomeFeedViewModel
import io.paku.climblog.presentation.ui.profile.ProfileScreen
import io.paku.climblog.presentation.ui.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.search.SearchScreen
import io.paku.climblog.presentation.ui.search.SearchViewModel
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinInject(),
    onUploadClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onVideoClick: (Long) -> Unit,
    onUserClick: (Long) -> Unit,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val state by viewModel.state
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            if (selectedTab == 0) { // Only show on Home
                CenterAlignedTopAppBar(
                    title = { Text("ClimbLog") },
                    actions = {
                        IconButton(onClick = onNotificationClick) {
                            Box {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                                if (state.hasUnreadNotifications) {
                                    Surface(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .align(Alignment.TopEnd),
                                        shape = CircleShape,
                                        color = Color.Red
                                    ) {}
                                }
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> {
                    val homeViewModel: HomeFeedViewModel = koinInject()
                    HomeFeedScreen(viewModel = homeViewModel)
                }
                1 -> {
                    val searchViewModel: SearchViewModel = koinInject()
                    SearchScreen(
                        viewModel = searchViewModel,
                        onUserClick = onUserClick,
                        onVideoClick = onVideoClick
                    )
                }
                2 -> {
                    val profileViewModel: ProfileViewModel = koinInject()
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onUploadClick = onUploadClick,
                        onVideoClick = onVideoClick,
                        onMenuClick = onMenuClick,
                        onEditClick = onEditClick
                    )
                }
            }
        }
    }
}
