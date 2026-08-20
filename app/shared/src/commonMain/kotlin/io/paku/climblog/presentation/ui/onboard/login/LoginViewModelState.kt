package io.paku.climblog.presentation.ui.onboard.login

import io.paku.climblog.presentation.base.State

data class LoginViewModelState(
    val loginError: String? = null
): State