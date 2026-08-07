package io.paku.kmp_template.business.domain.di

import io.paku.kmp_template.business.domain.interactors.session.FetchSessionUseCase
import org.koin.dsl.module

val DomainModule = module {
    single { FetchSessionUseCase(get()) }
}