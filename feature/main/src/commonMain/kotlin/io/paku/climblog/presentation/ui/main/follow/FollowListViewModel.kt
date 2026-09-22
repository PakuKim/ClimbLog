package io.paku.climblog.presentation.ui.main.follow

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.paku.climblog.navigation.AppNavigation
import io.paku.climblog.navigation.FollowListType
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class FollowListViewModelState(
    val users: List<io.paku.climblog.domain.model.user.User> = emptyList(),
    val isLoading: Boolean = false,
    val type: FollowListType = FollowListType.FOLLOWERS
) : ViewModelState

internal class FollowListViewModel(
    savedStateHandle: SavedStateHandle,
    private val getFollowersUseCase: io.paku.climblog.domain.interactors.user.GetFollowersUseCase,
    private val getFollowingUseCase: io.paku.climblog.domain.interactors.user.GetFollowingUseCase
) : BaseViewModel<FollowListViewModelState, ViewModelEvent, Nothing>() {
    private val args: AppNavigation.FollowList = savedStateHandle.toRoute()

    override fun createInitialState() = FollowListViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {}

    private fun loadUsers() = launch {
        updateState { copy(isLoading = true) }
        val result = when (args.type) {
            FollowListType.FOLLOWERS -> getFollowersUseCase(args.userId)
            FollowListType.FOLLOWING -> getFollowingUseCase(args.userId)
        }
        
        result.onSuccess { users ->
            updateState { copy(users = users, isLoading = false) }
        }.onFailure {
            updateState { copy(isLoading = false) }
        }
    }

    init {
        loadUsers()
    }
}
