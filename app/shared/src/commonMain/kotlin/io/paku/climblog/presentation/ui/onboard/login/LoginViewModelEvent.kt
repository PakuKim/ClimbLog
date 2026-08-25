package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.presentation.base.ViewModelEvent

sealed class LoginViewModelEvent: ViewModelEvent {
    data class OnSocialLoginClick(val provider: SocialLoginType) : LoginViewModelEvent()
}