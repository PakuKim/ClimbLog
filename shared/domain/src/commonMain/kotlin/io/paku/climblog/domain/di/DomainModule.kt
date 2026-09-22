package io.paku.climblog.domain.di

import io.paku.climblog.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.domain.interactors.auth.SocialLoginUseCase
import io.paku.climblog.domain.interactors.auth.SocialRegisterUseCase
import io.paku.climblog.domain.interactors.notification.CheckUnreadNotificationsUseCase
import io.paku.climblog.domain.interactors.notification.GetNotificationsUseCase
import io.paku.climblog.domain.interactors.notification.SendDeviceTokenUseCase
import io.paku.climblog.domain.interactors.session.FetchSessionUseCase
import io.paku.climblog.domain.interactors.user.CheckHandleUseCase
import io.paku.climblog.domain.interactors.user.DeleteUserUseCase
import io.paku.climblog.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.domain.interactors.user.GetFollowStatusUseCase
import io.paku.climblog.domain.interactors.user.GetFollowersUseCase
import io.paku.climblog.domain.interactors.user.GetFollowingUseCase
import io.paku.climblog.domain.interactors.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactors.user.SearchUsersUseCase
import io.paku.climblog.domain.interactors.user.ToggleFollowUseCase
import io.paku.climblog.domain.interactors.user.UpdateProfileUseCase
import io.paku.climblog.domain.interactors.video.GetCommentsUseCase
import io.paku.climblog.domain.interactors.video.GetRandomVideosUseCase
import io.paku.climblog.domain.interactors.video.GetSingleVideoUseCase
import io.paku.climblog.domain.interactors.video.GetUserVideosUseCase
import io.paku.climblog.domain.interactors.video.GetVideoFeedUseCase
import io.paku.climblog.domain.interactors.video.PostCommentUseCase
import io.paku.climblog.domain.interactors.video.SearchVideosUseCase
import io.paku.climblog.domain.interactors.video.ToggleLikeUseCase
import io.paku.climblog.domain.interactors.video.UploadVideoUseCase
import org.koin.dsl.module

val DomainModule = module {
    single { FetchSessionUseCase(get()) }
    single { SocialLoginUseCase(get(), get()) }
    single { LogoutUseCase(get(), get()) }
    single { SocialRegisterUseCase(get(), get()) }
    single { FetchUserUseCase(get()) }
    single { CheckHandleUseCase(get()) }
    single { UpdateProfileUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { UploadVideoUseCase(get(), get()) }
    single { SendDeviceTokenUseCase(get()) }
    single { CheckUnreadNotificationsUseCase(get()) }
    single { GetNotificationsUseCase(get()) }
    
    // User
    single { SearchUsersUseCase(get()) }
    single { GetUserProfileUseCase(get()) }
    single { ToggleFollowUseCase(get()) }
    single { GetFollowersUseCase(get()) }
    single { GetFollowingUseCase(get()) }
    single { GetFollowStatusUseCase(get()) }

    // Video
    single { GetVideoFeedUseCase(get()) }
    single { ToggleLikeUseCase(get()) }
    single { GetCommentsUseCase(get()) }
    single { PostCommentUseCase(get()) }
    single { GetRandomVideosUseCase(get()) }
    single { GetUserVideosUseCase(get()) }
    single { SearchVideosUseCase(get()) }
    single { GetSingleVideoUseCase(get()) }
}
