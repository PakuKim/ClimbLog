package io.paku.climblog.presentation.ui.main.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import io.paku.climblog.domain.model.user.User
import io.paku.climblog.domain.model.user.UserProfile
import io.paku.climblog.presentation.component.PreviewWrapper
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.component.VideoThumbnailItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ProfileRoute(
    viewModel: ProfileViewModel = koinViewModel(),
    onUploadClick: () -> Unit,
    onVideoClick: (Long) -> Unit,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit,
    onFollowersClick: (Long) -> Unit,
    onFollowingClick: (Long) -> Unit
) {
    val state by viewModel.state

    ProfileScreen(
        state = state,
        onUploadClick = onUploadClick,
        onVideoClick = onVideoClick,
        onMenuClick = onMenuClick,
        onEditClick = onEditClick,
        onFollowClick = { viewModel.onEvent(ProfileViewModelEvent.ToggleFollow) },
        onFollowersClick = onFollowersClick,
        onFollowingClick = onFollowingClick
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileViewModelState,
    onUploadClick: () -> Unit = {},
    onVideoClick: (Long) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onFollowersClick: (Long) -> Unit = {},
    onFollowingClick: (Long) -> Unit = {}
) {
    val profile = state.userProfile ?: return
    val pagingItems = state.videoPagingData?.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = profile.user.handle.ifBlank { profile.user.name },
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
                onEditClick = onEditClick,
                onFollowersClick = { onFollowersClick(profile.user.id) }
            ) { onFollowingClick(profile.user.id) }

            ClimbingSpecCard(
                age = profile.user.age,
                height = profile.user.height,
                armReach = profile.user.armReach,
                gender = profile.user.gender
            )

            HorizontalDivider(modifier = Modifier.padding(top = 16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                pagingItems?.let { items ->
                    items(items.itemCount) { index ->
                        items[index]?.let { video ->
                            VideoThumbnailItem(video) { onVideoClick(video.id) }
                        }
                    }
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
    onEditClick: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            // TODO: Use Coil for profile image
            AsyncImage(
                model = profile.user.profilePhotoUrl,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
                    .clip(CircleShape),
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileStatItem(label = "게시물", count = profile.videoCount.toString())
                ProfileStatItem(label = "팔로워", count = profile.followerCount.toString(), onClick = onFollowersClick)
                ProfileStatItem(label = "팔로잉", count = profile.followingCount.toString(), onClick = onFollowingClick)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        @Suppress("DEPRECATION")
        Text(
            text = profile.user.name,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isMyProfile) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("프로필 편집", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { /* Share Profile */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("프로필 공유", fontSize = 14.sp)
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onFollowClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (profile.isFollowing) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                        contentColor = if (profile.isFollowing) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                    ),
                    enabled = !isFollowingInProgress
                ) {
                    Text(if (profile.isFollowing) "팔로잉" else "팔로우", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { /* Share Profile */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
                ) {
                    Text("프로필 공유", fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileStatItem(label: String, count: String, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = count, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
fun ClimbingSpecCard(
    height: Int,
    armReach: Int,
    age: Int,
    gender: String
) {
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
            SpecItem(label = "성별", value = if (gender == "MALE") "남" else "여")
            SpecItem(label = "키", value = "$height cm")
            SpecItem(label = "암리치", value = "$armReach cm")
            SpecItem(label = "나이", value = "$age 세")
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
@Preview
private fun ClimbingSpecPreview() {
    PreviewWrapper {
        ClimbingSpecCard(
            height = 180,
            armReach = 180,
            age = 20,
            gender = "MALE"
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    PreviewWrapper {
        ProfileScreen(
            state = ProfileViewModelState(
                userProfile = UserProfile(
                    user = User(
                        id = 1,
                        name = "홍길동",
                        handle = "gildong",
                        age = 20,
                        height = 180,
                        armReach = 190,
                        gender = "MALE",
                        profilePhotoUrl = null
                    ),
                    followerCount = 100,
                    followingCount = 200,
                    videoCount = 50,
                    isFollowing = false
                ),
                isMyProfile = true
            )
        )
    }
}
