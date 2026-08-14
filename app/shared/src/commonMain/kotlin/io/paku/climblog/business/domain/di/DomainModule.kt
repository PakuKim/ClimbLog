package io.paku.climblog.business.domain.di

import io.paku.climblog.business.domain.interactors.CheckHandleUseCase
import io.paku.climblog.business.domain.interactors.RegisterUserUseCase
import io.paku.climblog.business.domain.interactors.auth.LoginUseCase
import io.paku.climblog.business.domain.interactors.session.FetchSessionUseCase
import io.paku.climblog.business.domain.interactors.user.FetchUserUseCase
import io.paku.climblog.business.domain.interactors.video.UploadVideoUseCase
import org.koin.dsl.module

val DomainModule = module {
    single { FetchSessionUseCase(get()) }
    single { LoginUseCase(get()) }
    single { FetchUserUseCase(get()) }
    single { CheckHandleUseCase(get()) }
    single { RegisterUserUseCase(get()) }
    single { UploadVideoUseCase(get()) }
}
