package io.paku.climblog.di

import io.paku.climblog.business.data.di.DataModule
import io.paku.climblog.business.domain.di.DomainModule
import io.paku.climblog.business.local.di.LocalModule
import io.paku.climblog.business.remote.di.RemoteModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.main.di.MainModule
import io.paku.climblog.presentation.ui.onboard.di.OnboardModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    includes(
        DomainModule,
        DataModule,
        LocalModule,
        RemoteModule,
        MainModule,
        OnboardModule,
        sharedPlatformModule,
    )

    viewModelOf(::AppViewModel)
}