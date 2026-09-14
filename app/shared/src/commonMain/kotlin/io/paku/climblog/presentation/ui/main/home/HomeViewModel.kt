package io.paku.climblog.presentation.ui.main.home

import androidx.lifecycle.SavedStateHandle
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data object HomeState: ViewModelState

sealed class HomeEvent: ViewModelEvent

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle
): BaseViewModel<HomeState, HomeEvent, Nothing>() {
    override fun createInitialState(): HomeState {
        TODO("Not yet implemented")
    }

    override fun createTriggerEvent(event: ViewModelEvent) {
        TODO("Not yet implemented")
    }
}