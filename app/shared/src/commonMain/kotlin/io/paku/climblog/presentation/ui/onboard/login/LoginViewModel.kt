package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.AuthRepository
import io.paku.climblog.business.domain.SocialAuthManager
import io.paku.climblog.business.domain.model.SocialProvider
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class LoginState(
    val isRegistered: Boolean? = null,
    val socialEmail: String = "",
    val socialName: String = "",
    val loginError: String? = null
) : State

sealed class LoginEvent : Event {
    data class OnSocialLoginClick(val provider: SocialProvider) : LoginEvent()
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val socialAuthManager: SocialAuthManager
) : BaseViewModel<LoginState, LoginEvent>() {

    override fun createInitialState(): LoginState = LoginState()

    override fun createTriggerEvent(event: Event) {
        if (event is LoginEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnSocialLoginClick -> performSocialLogin(event.provider)
        }
    }

    private fun performSocialLogin(provider: SocialProvider) = launchWithLoading {
        socialAuthManager.login(provider).onSuccess { result ->
            authRepository.socialLogin(
                provider = result.provider.name,
                accessToken = result.accessToken,
                idToken = result.idToken
            ).onSuccess { isRegistered ->
                updateState { 
                    copy(
                        isRegistered = isRegistered, 
                        socialEmail = result.email,
                        socialName = result.name,
                        loginError = null
                    ) 
                }
            }.onFailure { error ->
                updateState { copy(loginError = error.message ?: "Server login failed") }
            }
        }.onFailure { error ->
            updateState { copy(loginError = error.message ?: "Social login failed") }
        }
    }
}
