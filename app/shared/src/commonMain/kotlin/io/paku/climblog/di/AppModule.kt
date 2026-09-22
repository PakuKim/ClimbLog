package io.paku.climblog.di

import io.paku.climblog.local.di.LocalModule
import io.paku.climblog.platform.di.sharedPlatformModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.main.di.MainModule
import io.paku.climblog.presentation.ui.onboard.di.OnboardModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    includes(
        _root_ide_package_.io.paku.climblog.domain.di.DomainModule,
        io.paku.climblog.data.di.DataModule,
        LocalModule,
        io.paku.climblog.remote.di.RemoteModule,
        MainModule,
        OnboardModule,
        sharedPlatformModule,
    )

    viewModelOf(::AppViewModel)
}