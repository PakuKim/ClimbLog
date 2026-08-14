package io.paku.climblog.di

import io.paku.climblog.business.data.di.DataModule
import io.paku.climblog.business.domain.di.DomainModule
import io.paku.climblog.business.local.di.LocalModule
import io.paku.climblog.business.remote.di.RemoteModule
import io.paku.climblog.core.platformDataStoreModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.home.HomeFeedViewModel
import io.paku.climblog.presentation.ui.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import io.paku.climblog.presentation.ui.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.search.SearchViewModel
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
    factory { AppViewModel(get(), get(), get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { HomeFeedViewModel(get()) }
    factory { SearchViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { NotificationViewModel(get()) }
}
