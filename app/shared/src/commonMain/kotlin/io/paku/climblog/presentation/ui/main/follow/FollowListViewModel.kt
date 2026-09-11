package io.paku.climblog.presentation.ui.main.follow

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import io.paku.climblog.business.domain.interactors.user.GetFollowersUseCase
import io.paku.climblog.business.domain.interactors.user.GetFollowingUseCase
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.navigation.FollowListType

data class FollowListViewModelState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val type: FollowListType = FollowListType.FOLLOWERS
) : ViewModelState

internal class FollowListViewModel(
    savedStateHandle: SavedStateHandle,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase
) : BaseViewModel<FollowListViewModelState, ViewModelEvent, Nothing>() {
    private val args: AppNavigation.FollowList = savedStateHandle.toRoute()

    override fun createInitialState(): FollowListViewModelState = FollowListViewModelState(
        type = args.type
    )

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
