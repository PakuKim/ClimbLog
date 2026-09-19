package io.paku.climblog.presentation.ui.main.follow

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.component.UserItem
import io.paku.climblog.presentation.navigation.FollowListType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FollowListRoute(
    viewModel: FollowListViewModel = koinViewModel(),
    onUserClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state

    FollowListScreen(
        state = state,
        onUserClick = onUserClick,
        onBackClick = onBackClick
    )
}

@Composable
private fun FollowListScreen(
    state: FollowListViewModelState,
    onUserClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = if (state.type == FollowListType.FOLLOWERS) "Followers" else "Following",
                onNavClick = onBackClick
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.users) { user ->
                        UserItem(user = user) {
                            onUserClick(user.id)
                        }
                    }
                }
            }
        }
    }
}
