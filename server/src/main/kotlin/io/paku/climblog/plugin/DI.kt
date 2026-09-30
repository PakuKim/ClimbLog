package io.paku.climblog.plugin

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.paku.climblog.data.NotificationRepositoryImpl
import io.paku.climblog.data.RefreshTokenRepositoryImpl
import io.paku.climblog.data.UserFollowRepositoryImpl
import io.paku.climblog.data.UserRepositoryImpl
import io.paku.climblog.data.VideoCommentRepositoryImpl
import io.paku.climblog.data.VideoLikeRepositoryImpl
import io.paku.climblog.data.VideoRepositoryImpl
import io.paku.climblog.data.provider.BCryptEncodeProviderImpl
import io.paku.climblog.data.provider.JwtTokenProviderImpl
import io.paku.climblog.data.provider.MediaConvertProviderImpl
import io.paku.climblog.data.provider.PushProviderImpl
import io.paku.climblog.data.provider.S3ProviderImpl
import io.paku.climblog.data.redis.RedisManager
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.RefreshTokenRepository
import io.paku.climblog.domain.UserFollowRepository
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.VideoCommentRepository
import io.paku.climblog.domain.VideoLikeRepository
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.interactor.auth.LogoutUseCase
import io.paku.climblog.domain.interactor.auth.RefreshTokenUseCase
import io.paku.climblog.domain.interactor.auth.SocialLoginUseCase
import io.paku.climblog.domain.interactor.auth.SocialRegisterUseCase
import io.paku.climblog.domain.interactor.auth.VerifySocialTokenUseCase
import io.paku.climblog.domain.interactor.notification.CheckUnreadNotificationsUseCase
import io.paku.climblog.domain.interactor.notification.GetNotificationsUseCase
import io.paku.climblog.domain.interactor.notification.MarkNotificationsAsReadUseCase
import io.paku.climblog.domain.interactor.notification.SaveDeviceTokenUseCase
import io.paku.climblog.domain.interactor.notification.SendNotificationUseCase
import io.paku.climblog.domain.interactor.user.CheckHandleUseCase
import io.paku.climblog.domain.interactor.user.DeleteUserUseCase
import io.paku.climblog.domain.interactor.user.FollowUserUseCase
import io.paku.climblog.domain.interactor.user.GetFollowStatusUseCase
import io.paku.climblog.domain.interactor.user.GetFollowersUseCase
import io.paku.climblog.domain.interactor.user.GetFollowingUseCase
import io.paku.climblog.domain.interactor.user.GetUserProfileUseCase
import io.paku.climblog.domain.interactor.user.GetUserUseCase
import io.paku.climblog.domain.interactor.user.SearchUsersUseCase
import io.paku.climblog.domain.interactor.user.UnfollowUserUseCase
import io.paku.climblog.domain.interactor.user.UpdateUserUseCase
import io.paku.climblog.domain.interactor.video.GetRandomVideosUseCase
import io.paku.climblog.domain.interactor.video.GetVideoCommentsUseCase
import io.paku.climblog.domain.interactor.video.GetVideoListUseCase
import io.paku.climblog.domain.interactor.video.PostCommentUseCase
import io.paku.climblog.domain.interactor.video.RegisterVideoUseCase
import io.paku.climblog.domain.interactor.video.ToggleLikeUseCase
import io.paku.climblog.domain.interactor.video.UpdateVideoStatusUseCase
import io.paku.climblog.domain.provider.BCryptEncodeProvider
import io.paku.climblog.domain.provider.JwtTokenProvider
import io.paku.climblog.domain.provider.MediaConvertProvider
import io.paku.climblog.domain.provider.PushProvider
import io.paku.climblog.domain.provider.S3Provider
import kotlinx.serialization.json.Json
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
    val awsMediaConvertRoleArn = environment.config.property("aws.mediaConvertRoleArn").getString()
    val awsMediaConvertQueueArn = environment.config.property("aws.mediaConvertQueueArn").getString()

    install(Koin) {
        modules(
            appModule(
                redisUrl = "redis://$redisHost:$redisPort",
                jwtSecret = jwtSecret,
                jwtIssuer = jwtIssuer,
                jwtAudience = jwtAudience,
                awsAccessKey = awsAccessKey,
                awsSecretKey = awsSecretKey,
                awsRegion = awsRegion,
                awsMediaConvertRoleArn = awsMediaConvertRoleArn,
                awsMediaConvertQueueArn = awsMediaConvertQueueArn
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
    awsRegion: String,
    awsMediaConvertRoleArn: String,
    awsMediaConvertQueueArn: String
) = module {
    // Data
    single { RedisManager(redisUrl) }.onClose { redisManager ->
        redisManager?.close()
    }
    single<RedisCoroutinesCommands<String, String>> { get<RedisManager>().commands }
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
    single<VideoCommentRepository> { VideoCommentRepositoryImpl() }
    single<VideoLikeRepository> { VideoLikeRepositoryImpl() }
    single<UserFollowRepository> { UserFollowRepositoryImpl() }
    single<NotificationRepository> { NotificationRepositoryImpl() }
    single<S3Provider> { S3ProviderImpl(awsAccessKey, awsSecretKey, awsRegion) }
    single<MediaConvertProvider> {
        MediaConvertProviderImpl(
            accessKey = awsAccessKey,
            secretKey = awsSecretKey,
            region = awsRegion,
            roleArn = awsMediaConvertRoleArn,
            queueArn = awsMediaConvertQueueArn
        )
    }
    single<PushProvider> { PushProviderImpl() }

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }

    // Domain
    factory { RefreshTokenUseCase(get(), get()) }
    factory { VerifySocialTokenUseCase(get()) }
    factory { SocialLoginUseCase(get(), get(), get()) }
    factory { SocialRegisterUseCase(get(), get()) }
    factory { LogoutUseCase(get()) }
    factory { GetUserUseCase(get()) }
    factory { CheckHandleUseCase(get()) }
    factory { SearchUsersUseCase(get()) }
    factory { GetUserProfileUseCase(get(), get(), get()) }
    factory { FollowUserUseCase(get(), get()) }
    factory { UnfollowUserUseCase(get()) }
    factory { GetFollowersUseCase(get(), get()) }
    factory { GetFollowingUseCase(get(), get()) }
    factory { GetFollowStatusUseCase(get()) }
    factory { UpdateUserUseCase(get()) }
    factory { DeleteUserUseCase(get(), get()) }
    factory { GetRandomVideosUseCase(get()) }
    factory { GetVideoListUseCase(get()) }
    factory { GetVideoCommentsUseCase(get()) }
    factory { RegisterVideoUseCase(get(), get()) }
    factory { UpdateVideoStatusUseCase(get()) }
    factory { SendNotificationUseCase(get(), get(), get()) }
    factory { GetNotificationsUseCase(get()) }
    factory { CheckUnreadNotificationsUseCase(get()) }
    factory { SaveDeviceTokenUseCase(get()) }
    factory { MarkNotificationsAsReadUseCase(get()) }
    factory { ToggleLikeUseCase(get(), get(), get()) }
    factory { PostCommentUseCase(get(), get(), get()) }
}
