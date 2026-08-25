package io.paku.climblog.presentation.ui.main.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.UserProfile
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

    LaunchedEffect(userId) {
        viewModel.onEvent(ProfileViewModelEvent.LoadProfile(userId, isMyProfile = false))
    }

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

@Composable
fun ClimbingSpecCard(profile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SpecItem(label = "키", value = "${profile.user.height ?: "-"} cm")
            SpecItem(label = "암리치", value = "${profile.user.armReach ?: "-"} cm")
            SpecItem(label = "나이", value = "${profile.user.age ?: "-"} 세")
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
