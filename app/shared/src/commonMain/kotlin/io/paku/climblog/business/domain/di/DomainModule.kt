package io.paku.climblog.business.domain.di

import io.paku.climblog.business.domain.interactors.CheckHandleUseCase
import io.paku.climblog.business.domain.interactors.RegisterUserUseCase
import io.paku.climblog.business.domain.interactors.auth.LoginUseCase
import io.paku.climblog.business.domain.interactors.auth.LogoutUseCase
import io.paku.climblog.business.domain.interactors.notification.SendDeviceTokenUseCase
import io.paku.climblog.business.domain.interactors.session.FetchSessionUseCase
import io.paku.climblog.business.domain.interactors.user.DeleteUserUseCase
import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.business.domain.interactors.user.UpdateProfileUseCase
import io.paku.climblog.business.domain.interactors.video.UploadVideoUseCase
import org.koin.dsl.module

val DomainModule = module {
    single { FetchSessionUseCase(get()) }
    single { LoginUseCase(get()) }
    single { LogoutUseCase(get()) }
    single { FetchUserUseCase(get()) }
    single { CheckHandleUseCase(get()) }
    single { RegisterUserUseCase(get()) }
    single { UpdateProfileUseCase(get()) }
    single { DeleteUserUseCase(get()) }
    single { UploadVideoUseCase(get()) }
    single { SendDeviceTokenUseCase(get()) }
}
