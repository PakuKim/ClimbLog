package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.core.CommonException
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelAction
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class LoginViewModelState(
    val loginError: String? = null
): ViewModelState

sealed class LoginViewModelEvent: ViewModelEvent {
    data class OnSocialLoginClick(
        val provider: io.paku.climblog.domain.model.social.SocialLoginType
    ) : LoginViewModelEvent()
}
sealed class LoginViewModelAction: ViewModelAction {
    data class NavigateToRegister(
        val socialLoginType: io.paku.climblog.domain.model.social.SocialLoginType
    ): LoginViewModelAction()
}

internal class LoginViewModel(
    private val socialLoginUseCase: io.paku.climblog.domain.interactors.auth.SocialLoginUseCase
) : BaseViewModel<LoginViewModelState, LoginViewModelEvent, LoginViewModelAction>() {
    override fun createInitialState(): LoginViewModelState = LoginViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is LoginViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: LoginViewModelEvent) {
        when (event) {
            is LoginViewModelEvent.OnSocialLoginClick -> socialLogin(event.provider)
        }
    }

    private fun socialLogin(type: io.paku.climblog.domain.model.social.SocialLoginType) = launchWithLoading {
        socialLoginUseCase(type)
    }.invokeOnCompletion {
        if (it is CommonException && it.code == 404) {
            setAction { LoginViewModelAction.NavigateToRegister(type) }
        }
    }
}
