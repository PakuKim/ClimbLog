package io.paku.climblog.presentation.ui.main.profile

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.user.UserProfile
import io.paku.climblog.presentation.component.PreviewWrapper
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.ui.main.search.VideoThumbnailItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRoute(
    viewModel: ProfileViewModel = koinViewModel(),
    onUploadClick: () -> Unit,
    onVideoClick: (Long) -> Unit,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val state by viewModel.state

    ProfileScreen(
        state = state,
        onUploadClick = onUploadClick,
        onVideoClick = onVideoClick,
        onMenuClick = onMenuClick,
        onEditClick = onEditClick,
        onFollowClick = { viewModel.onEvent(ProfileViewModelEvent.ToggleFollow) }
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileViewModelState,
    onUploadClick: () -> Unit,
    onVideoClick: (Long) -> Unit,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit,
    onFollowClick: () -> Unit
) {
    val profile = state.userProfile ?: return

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = profile.user.handle,
                actions = {
                    if (state.isMyProfile) {
                        IconButton(onClick = onUploadClick) {
                            Icon(Icons.Default.AddBox, contentDescription = "Upload")
                        }
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            ProfileHeader(
                profile = profile,
                isMyProfile = state.isMyProfile,
                isFollowingInProgress = state.isFollowingInProgress,
                onFollowClick = onFollowClick,
                onEditClick = onEditClick
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

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

@Composable
fun ProfileHeader(
    profile: UserProfile,
    isMyProfile: Boolean,
    isFollowingInProgress: Boolean,
    onFollowClick: () -> Unit,
    onEditClick: () -> Unit
) {
    ClimbingSpecCard(
        profile = profile
    )
   /* Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(label = "Posts", count = profile.videoCount.toString())
            StatItem(label = "Followers", count = profile.followerCount.toString())
            StatItem(label = "Following", count = profile.followingCount.toString())
        }
    }*/

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(profile.user.name, fontWeight = FontWeight.Bold)
        profile.user.age.let { Text("Age: $it", fontSize = 14.sp) }
        Row {
            profile.user.height?.let { Text("H: ${it}cm ", fontSize = 14.sp) }
            profile.user.armReach?.let { Text("A: ${it}cm", fontSize = 14.sp) }
        }
    }

    if (isMyProfile) {
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Edit Profile", color = MaterialTheme.colorScheme.onSurface)
        }
    } else {
        Button(
            onClick = onFollowClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (profile.isFollowing) Color.LightGray else MaterialTheme.colorScheme.primary,
                contentColor = if (profile.isFollowing) Color.Black else Color.White
            ),
            enabled = !isFollowingInProgress
        ) {
            Text(if (profile.isFollowing) "Following" else "Follow")
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
            SpecItem(label = "키", value = "${profile.user.height} cm")
            SpecItem(label = "암리치", value = "${profile.user.armReach} cm")
            SpecItem(label = "나이", value = "${profile.user.age} 세")
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

@Composable
@Preview
private fun ProfileScreenPreview() {
    PreviewWrapper {
        ProfileScreen(
            state = ProfileViewModelState(),
            onUploadClick = {},
            onVideoClick = {},
            onMenuClick = {},
            onEditClick = {},
            onFollowClick = {}
        )
    }
}