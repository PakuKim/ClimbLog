package io.paku.climblog.presentation.ui.main.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.ui.main.search.VideoThumbnailItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UserProfileScreen(
    userId: Long,
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onVideoClick: (Long) -> Unit
) {
    val state = viewModel.state.value
    val profile = state.userProfile


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(profile?.user?.handle ?: "Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (profile == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Header
                ProfileHeader(
                    profile = profile, 
                    isMyProfile = false, 
                    isFollowingInProgress = state.isFollowingInProgress,
                    onFollowClick = { viewModel.onEvent(ProfileViewModelEvent.ToggleFollow) },
                    onEditClick = { }
                )

                // Climbing Spec Card
                ClimbingSpecCard(profile)

                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

                // Video Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(1.dp),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(state.userVideos) { video ->
                        VideoThumbnailItem(video) { onVideoClick(video.id) }
                    }
                }
            }
        }
    }
}
