package io.paku.climblog.presentation.ui.onboard.di

import io.paku.climblog.presentation.ui.onboard.login.LoginViewModel
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val OnboardModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}
