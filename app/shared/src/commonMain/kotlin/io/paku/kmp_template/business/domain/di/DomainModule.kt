package io.paku.kmp_template.business.domain.di

import io.paku.kmp_template.business.domain.interactors.auth.LoginUseCase
import io.paku.kmp_template.business.domain.interactors.session.FetchSessionUseCase
import io.paku.kmp_template.business.domain.interactors.user.FetchUserUseCase
import org.koin.dsl.module

val DomainModule = module {
    single { FetchSessionUseCase(get()) }
    single { LoginUseCase(get()) }
    single { FetchUserUseCase(get()) }
}