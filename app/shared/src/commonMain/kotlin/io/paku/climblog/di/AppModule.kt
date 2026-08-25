package io.paku.climblog.di

import io.paku.climblog.business.data.di.DataModule
import io.paku.climblog.business.domain.di.DomainModule
import io.paku.climblog.business.local.di.LocalModule
import io.paku.climblog.business.remote.di.RemoteModule
import io.paku.climblog.core.platformDataStoreModule
import io.paku.climblog.core.platformSocialModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.main.MainViewModel
import io.paku.climblog.presentation.ui.main.home.HomeFeedViewModel
import io.paku.climblog.presentation.ui.main.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.main.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileViewModel
import io.paku.climblog.presentation.ui.main.search.SearchViewModel
import io.paku.climblog.presentation.ui.main.settings.SettingsViewModel
import io.paku.climblog.presentation.ui.main.upload.VideoUploadViewModel
import io.paku.climblog.presentation.ui.onboard.login.LoginViewModel
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

fun appModule() = module {
    includes(
        uiModule,
        DomainModule,
        DataModule,
        LocalModule,
        RemoteModule,
        platformDataStoreModule,
        platformSocialModule
    )
}

val uiModule = module {
    factory { AppViewModel(get(), get()) }
    factory { LoginViewModel(get()) }
    viewModelOf(::RegisterViewModel)
    factory { HomeFeedViewModel(get()) }
    factory { SearchViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { EditProfileViewModel(get(), get()) }
    factory { SettingsViewModel(get(), get()) }
    factory { NotificationViewModel(get()) }
    factory { VideoUploadViewModel(get()) }
    factory { MainViewModel(get()) }
}
