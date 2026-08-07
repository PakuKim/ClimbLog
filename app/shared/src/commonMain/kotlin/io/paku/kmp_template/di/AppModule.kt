package io.paku.kmp_template.di

import io.paku.kmp_template.AppViewModel
import io.paku.kmp_template.business.data.di.DataModule
import io.paku.kmp_template.business.domain.di.DomainModule
import io.paku.kmp_template.business.local.di.LocalModule
import io.paku.kmp_template.business.remote.di.RemoteModule
import io.paku.kmp_template.core.platformDataStoreModule
import org.koin.dsl.module

fun appModule() = module {
    includes(
        uiModule,
        DomainModule,
        DataModule,
        LocalModule,
        RemoteModule,
        platformDataStoreModule,
    )
}

val uiModule = module {
    factory { AppViewModel(get()) }
}