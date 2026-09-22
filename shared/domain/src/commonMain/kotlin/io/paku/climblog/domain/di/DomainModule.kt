package io.paku.climblog.domain.di

import org.koin.dsl.module

val DomainModule = module {
    single { _root_ide_package_.io.paku.climblog.domain.interactors.session.FetchSessionUseCase(get()) }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.auth.SocialLoginUseCase(
            get(),
            get()
        )
    }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.auth.LogoutUseCase(
            get(),
            get()
        )
    }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.auth.SocialRegisterUseCase(
            get(),
            get()
        )
    }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.FetchUserUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.CheckHandleUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.UpdateProfileUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.DeleteUserUseCase(get()) }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.video.UploadVideoUseCase(
            get(),
            get()
        )
    }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.notification.SendDeviceTokenUseCase(
            get()
        )
    }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.notification.CheckUnreadNotificationsUseCase(
            get()
        )
    }
    single {
        _root_ide_package_.io.paku.climblog.domain.interactors.notification.GetNotificationsUseCase(
            get()
        )
    }
    
    // User
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.SearchUsersUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.GetUserProfileUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.ToggleFollowUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.GetFollowersUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.GetFollowingUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.user.GetFollowStatusUseCase(get()) }

    // Video
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.GetVideoFeedUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.ToggleLikeUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.GetCommentsUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.PostCommentUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.GetRandomVideosUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.GetUserVideosUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.SearchVideosUseCase(get()) }
    single { _root_ide_package_.io.paku.climblog.domain.interactors.video.GetSingleVideoUseCase(get()) }
}
