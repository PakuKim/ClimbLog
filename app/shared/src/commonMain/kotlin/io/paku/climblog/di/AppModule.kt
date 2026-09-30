package io.paku.climblog.di

import io.paku.climblog.data.di.DataModule
import io.paku.climblog.domain.di.DomainModule
import io.paku.climblog.local.di.LocalModule
import io.paku.climblog.platform.di.sharedPlatformModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.main.di.MainModule
import io.paku.climblog.presentation.ui.onboard.di.OnboardModule
import io.paku.climblog.remote.di.RemoteModule
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