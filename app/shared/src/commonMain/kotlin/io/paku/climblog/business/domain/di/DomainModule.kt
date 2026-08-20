package io.paku.climblog.business.domain.di

import io.paku.climblog.business.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.business.domain.interactors.auth.SocialLoginUseCase
import io.paku.climblog.business.domain.interactors.auth.SocialRegisterUseCase
import io.paku.climblog.business.domain.interactors.notification.SendDeviceTokenUseCase
import io.paku.climblog.business.domain.interactors.session.FetchSessionUseCase
import io.paku.climblog.business.domain.interactors.user.CheckHandleUseCase
import io.paku.climblog.business.domain.interactors.user.DeleteUserUseCase
import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.business.domain.interactors.user.UpdateProfileUseCase
import io.paku.climblog.business.domain.interactors.video.UploadVideoUseCase
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
    single { UploadVideoUseCase(get()) }
    single { SendDeviceTokenUseCase(get()) }
}
