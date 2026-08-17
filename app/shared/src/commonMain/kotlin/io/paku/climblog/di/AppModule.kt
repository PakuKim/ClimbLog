package io.paku.climblog.di

import io.paku.climblog.business.data.di.DataModule
import io.paku.climblog.business.domain.SocialAuthManager
import io.paku.climblog.business.domain.di.DomainModule
import io.paku.climblog.business.local.di.LocalModule
import io.paku.climblog.business.remote.di.RemoteModule
import io.paku.climblog.core.platformDataStoreModule
import io.paku.climblog.presentation.AppViewModel
import io.paku.climblog.presentation.ui.home.HomeFeedViewModel
import io.paku.climblog.presentation.ui.main.MainViewModel
import io.paku.climblog.presentation.ui.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.onboard.login.LoginViewModel
import io.paku.climblog.presentation.ui.onboard.register.RegisterViewModel
import io.paku.climblog.presentation.ui.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.profile.edit.EditProfileViewModel
import io.paku.climblog.presentation.ui.search.SearchViewModel
import io.paku.climblog.presentation.ui.settings.SettingsViewModel
import io.paku.climblog.presentation.ui.upload.VideoUploadViewModel
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
    single { SocialAuthManager() }
    factory { AppViewModel(get(), get(), get(), get()) }
    factory { LoginViewModel(get(), get()) }
    factory { RegisterViewModel(get(), get()) }
    factory { HomeFeedViewModel(get()) }
    factory { SearchViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { EditProfileViewModel(get(), get()) }
    factory { SettingsViewModel(get(), get()) }
    factory { NotificationViewModel(get()) }
    factory { VideoUploadViewModel(get()) }
    factory { MainViewModel(get()) }
}
