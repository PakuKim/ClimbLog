package io.paku.climblog.presentation.ui.main.settings

import io.paku.climblog.business.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.business.domain.interactors.user.DeleteUserUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class SettingsViewModelState(
    val isLogoutSuccess: Boolean = false,
    val isDeleteAccountSuccess: Boolean = false
) : ViewModelState

sealed class SettingsViewModelEvent : ViewModelEvent {
    object OnLogoutClick : SettingsViewModelEvent()
    object OnDeleteAccountClick : SettingsViewModelEvent()
}

internal class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : BaseViewModel<SettingsViewModelState, SettingsViewModelEvent, Nothing>() {

    override fun createInitialState(): SettingsViewModelState = SettingsViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is SettingsViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: SettingsViewModelEvent) {
        when (event) {
            is SettingsViewModelEvent.OnLogoutClick -> logout()
            is SettingsViewModelEvent.OnDeleteAccountClick -> deleteAccount()
        }
    }

    private fun logout() = launchWithLoading {
        logoutUseCase()
    }

    private fun deleteAccount() = launchWithLoading {
        deleteUserUseCase()
    }
}
