package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.presentation.base.ViewModelAction

sealed class LoginViewModelAction: ViewModelAction {
    data class NavigateToRegister(val socialLoginType: SocialLoginType): LoginViewModelAction()
}