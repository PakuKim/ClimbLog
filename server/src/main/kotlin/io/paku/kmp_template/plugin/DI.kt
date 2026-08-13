package io.paku.kmp_template.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.paku.kmp_template.data.RefreshTokenRepositoryImpl
import io.paku.kmp_template.data.UserRepositoryImpl
import io.paku.kmp_template.data.redis.RedisManager
import io.paku.kmp_template.data.sercurity.BCryptEncodeProviderImpl
import io.paku.kmp_template.data.sercurity.JwtTokenProviderImpl
import io.paku.kmp_template.domain.RefreshTokenRepository
import io.paku.kmp_template.domain.UserRepository
import io.paku.kmp_template.domain.interactor.auth.CheckEmailUseCase
import io.paku.kmp_template.domain.interactor.auth.LoginUseCase
import io.paku.kmp_template.domain.interactor.auth.RefreshTokenUseCase
import io.paku.kmp_template.domain.interactor.auth.RegisterUseCase
import io.paku.kmp_template.domain.interactor.user.GetUserUseCase
import io.paku.kmp_template.domain.provider.BCryptEncodeProvider
import io.paku.kmp_template.domain.provider.JwtTokenProvider
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    val redisHost = environment.config.property("redis.host").getString()
    val redisPort = environment.config.property("redis.port").getString()

    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    install(Koin) {
        modules(
            appModule(
                redisUrl = "redis://$redisHost:$redisPort",
                jwtSecret = jwtSecret,
                jwtIssuer = jwtIssuer,
                jwtAudience = jwtAudience
            )
        )
    }
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
private fun appModule(
    redisUrl: String,
    jwtSecret: String,
    jwtIssuer: String,
    jwtAudience: String
) = module {
    // Data
    single { RedisManager(redisUrl) }.onClose { redisManager ->
        redisManager?.close()
    }
    single { get<RedisManager>().commands }
    single<RefreshTokenRepository> { RefreshTokenRepositoryImpl(get()) }
    single<BCryptEncodeProvider> { BCryptEncodeProviderImpl() }
    single<JwtTokenProvider> {
        JwtTokenProviderImpl(
            secret = jwtSecret,
            issuer = jwtIssuer,
            audience = jwtAudience
        )
    }
    single<UserRepository> { UserRepositoryImpl() }

    // Domain
    factory { RefreshTokenUseCase(get(), get()) }
    factory { CheckEmailUseCase(get()) }
    factory { LoginUseCase(get(), get(), get()) }
    factory { RegisterUseCase(get(), get()) }
    factory { GetUserUseCase(get()) }
}