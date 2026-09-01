package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.interactors.auth.SocialLoginUseCase
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.business.model.CommonException
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelAction
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class LoginViewModelState(
    val loginError: String? = null
): ViewModelState

sealed class LoginViewModelEvent: ViewModelEvent {
    data class OnSocialLoginClick(
        val provider: SocialLoginType
    ) : LoginViewModelEvent()
}
sealed class LoginViewModelAction: ViewModelAction {
    data class NavigateToRegister(
        val socialLoginType: SocialLoginType
    ): LoginViewModelAction()
}

internal class LoginViewModel(
    private val socialLoginUseCase: SocialLoginUseCase
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

    private fun socialLogin(type: SocialLoginType) = launchWithLoading {
        socialLoginUseCase(type)
    }.invokeOnCompletion {
        if (it is CommonException && it.code == 404) {
            setAction { LoginViewModelAction.NavigateToRegister(type) }
        }
    }
}
