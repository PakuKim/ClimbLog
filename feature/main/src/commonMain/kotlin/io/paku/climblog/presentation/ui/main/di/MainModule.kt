package io.paku.climblog.presentation.ui.main.di

import io.paku.climblog.presentation.ui.main.MainViewModel
import io.paku.climblog.presentation.ui.main.follow.FollowListViewModel
import io.paku.climblog.presentation.ui.main.notification.NotificationViewModel
import io.paku.climblog.presentation.ui.main.profile.ProfileViewModel
import io.paku.climblog.presentation.ui.main.profile.edit.EditProfileViewModel
import io.paku.climblog.presentation.ui.main.search.SearchViewModel
import io.paku.climblog.presentation.ui.main.settings.SettingsViewModel
import io.paku.climblog.presentation.ui.main.upload.VideoUploadViewModel
import io.paku.climblog.presentation.ui.main.video.SharedVideoViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val MainModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::FollowListViewModel)
    viewModelOf(::NotificationViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::VideoUploadViewModel)
    viewModelOf(::SharedVideoViewModel)
}
