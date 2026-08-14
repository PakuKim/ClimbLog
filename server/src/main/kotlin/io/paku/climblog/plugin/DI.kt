package io.paku.climblog.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.paku.climblog.data.CommentRepositoryImpl
import io.paku.climblog.data.FollowRepositoryImpl
import io.paku.climblog.data.LikeRepositoryImpl
import io.paku.climblog.data.NotificationRepositoryImpl
import io.paku.climblog.data.RefreshTokenRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.data.provider.S3ProviderImpl
import io.paku.climblog.data.redis.RedisManager
import io.paku.climblog.data.sercurity.BCryptEncodeProviderImpl
import io.paku.climblog.data.sercurity.JwtTokenProviderImpl
import io.paku.climblog.domain.CommentRepository
import io.paku.climblog.domain.FollowRepository
import io.paku.climblog.domain.LikeRepository
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.RefreshTokenRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.auth.CheckEmailUseCase
import io.paku.climblog.domain.interactor.auth.LoginUseCase
import io.paku.climblog.domain.interactor.auth.RefreshTokenUseCase
import io.paku.climblog.domain.interactor.auth.RegisterUseCase
import io.paku.climblog.domain.interactor.auth.SocialLoginUseCase
import io.paku.climblog.domain.interactor.user.CheckHandleUseCase
import io.paku.climblog.domain.interactor.user.CompleteRegistrationUseCase
import io.paku.climblog.domain.interactor.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactor.user.GetUserUseCase
import io.paku.climblog.domain.interactor.user.SearchUsersUseCase
import io.paku.climblog.domain.interactor.user.ToggleFollowUseCase
import io.paku.climblog.domain.interactor.video.GetRandomVideosUseCase
import io.paku.climblog.domain.provider.BCryptEncodeProvider
import io.paku.climblog.domain.provider.JwtTokenProvider
import io.paku.climblog.domain.provider.S3Provider
import org.koin.dsl.module
import org.koin.dsl.onClose
import org.koin.ktor.plugin.Koin

fun Application.configureDI() {
    val redisHost = environment.config.property("redis.host").getString()
    val redisPort = environment.config.property("redis.port").getString()

    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtIssuer = environment.config.property("jwt.issuer").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()

    val awsAccessKey = environment.config.property("aws.accessKey").getString()
    val awsSecretKey = environment.config.property("aws.secretKey").getString()
    val awsRegion = environment.config.property("aws.region").getString()

    install(Koin) {
        modules(
            appModule(
                redisUrl = "redis://$redisHost:$redisPort",
                jwtSecret = jwtSecret,
                jwtIssuer = jwtIssuer,
                jwtAudience = jwtAudience,
                awsAccessKey = awsAccessKey,
                awsSecretKey = awsSecretKey,
                awsRegion = awsRegion
            )
        )
    }
}

@OptIn(ExperimentalLettuceCoroutinesApi::class)
private fun appModule(
    redisUrl: String,
    jwtSecret: String,
    jwtIssuer: String,
    jwtAudience: String,
    awsAccessKey: String,
    awsSecretKey: String,
    awsRegion: String
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
    single<VideoRepository> { VideoRepositoryImpl() }
    single<CommentRepository> { CommentRepositoryImpl() }
    single<LikeRepository> { LikeRepositoryImpl() }
    single<FollowRepository> { FollowRepositoryImpl() }
    single<NotificationRepository> { NotificationRepositoryImpl() }
    single<S3Provider> { S3ProviderImpl(awsAccessKey, awsSecretKey, awsRegion) }

    // Domain
    factory { RefreshTokenUseCase(get(), get()) }
    factory { CheckEmailUseCase(get()) }
    factory { LoginUseCase(get(), get(), get()) }
    factory { RegisterUseCase(get(), get()) }
    factory { SocialLoginUseCase(get(), get()) }
    factory { GetUserUseCase(get()) }
    factory { CheckHandleUseCase(get()) }
    factory { CompleteRegistrationUseCase(get()) }
    factory { SearchUsersUseCase(get()) }
    factory { GetUserProfileUseCase(get(), get(), get()) }
    factory { ToggleFollowUseCase(get()) }
    factory { GetRandomVideosUseCase(get()) }
}
