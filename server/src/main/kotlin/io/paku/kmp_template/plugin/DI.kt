package io.paku.kmp_template.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.paku.kmp_template.data.UserRepositoryImpl
import io.paku.kmp_template.data.sercurity.BCryptEncodeProviderImpl
import io.paku.kmp_template.data.sercurity.JwtTokenProviderImpl
import io.paku.kmp_template.domain.UserRepository
import io.paku.kmp_template.domain.interactor.auth.CheckEmailUseCase
import io.paku.kmp_template.domain.interactor.auth.LoginUseCase
import io.paku.kmp_template.domain.interactor.auth.RegisterUseCase
import io.paku.kmp_template.domain.provider.BCryptEncodeProvider
import io.paku.kmp_template.domain.provider.JwtTokenProvider
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    install(Koin) {
        modules(appModule(jwtSecret, jwtIssuer, jwtAudience))
    }
}

fun appModule(
    jwtSecret: String,
    jwtIssuer: String,
    jwtAudience: String
) = module {
    // Data
    single<UserRepository> { UserRepositoryImpl() }
    single<BCryptEncodeProvider> { BCryptEncodeProviderImpl() }
    single<JwtTokenProvider> {
        JwtTokenProviderImpl(
            secret = jwtSecret,
            issuer = jwtIssuer,
            audience = jwtAudience
        )
    }
    // Domain
    factory { CheckEmailUseCase(get()) }
    factory { LoginUseCase(get(), get(), get()) }
    factory { RegisterUseCase(get(), get()) }
}