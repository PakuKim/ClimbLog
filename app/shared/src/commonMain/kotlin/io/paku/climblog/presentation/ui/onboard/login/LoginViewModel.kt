package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.interactors.auth.SocialLoginUseCase
import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event

internal class LoginViewModel(
    private val socialLoginUseCase: SocialLoginUseCase
) : BaseViewModel<LoginViewModelState, LoginViewModelEvent>() {

    override fun createInitialState(): LoginViewModelState = LoginViewModelState()

    override fun createTriggerEvent(event: Event) {
        if (event is LoginViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: LoginViewModelEvent) {
        when (event) {
            is LoginViewModelEvent.OnSocialLoginClick -> performSocialLogin(event.provider)
        }
    }

    private fun performSocialLogin(type: SocialAuthType) = launchWithLoading {
        socialLoginUseCase(type)
    }
}
