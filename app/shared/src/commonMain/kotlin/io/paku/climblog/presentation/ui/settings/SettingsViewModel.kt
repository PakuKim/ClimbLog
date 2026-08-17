package io.paku.climblog.presentation.ui.settings

import io.paku.climblog.business.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.business.domain.interactors.user.DeleteUserUseCase
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class SettingsState(
    val isLogoutSuccess: Boolean = false,
    val isDeleteAccountSuccess: Boolean = false
) : State

sealed class SettingsEvent : Event {
    object OnLogoutClick : SettingsEvent()
    object OnDeleteAccountClick : SettingsEvent()
}

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : BaseViewModel<SettingsState, SettingsEvent>() {

    override fun createInitialState(): SettingsState = SettingsState()

    override fun createTriggerEvent(event: Event) {
        if (event is SettingsEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnLogoutClick -> logout()
            is SettingsEvent.OnDeleteAccountClick -> deleteAccount()
        }
    }

    private fun logout() = launchWithLoading {
        logoutUseCase().onSuccess {
            updateState { copy(isLogoutSuccess = true) }
        }
    }

    private fun deleteAccount() = launchWithLoading {
        deleteUserUseCase().onSuccess {
            updateState { copy(isDeleteAccountSuccess = true) }
        }
    }
}
