package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.business.domain.model.SocialAuthType
import io.paku.climblog.presentation.base.Event

sealed class LoginViewModelEvent: Event {
    data class OnSocialLoginClick(val provider: SocialAuthType) : LoginViewModelEvent()
}