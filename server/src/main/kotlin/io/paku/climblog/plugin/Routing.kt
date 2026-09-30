package io.paku.climblog.plugin

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.paku.climblog.presentation.auth.authRoutes
import io.paku.climblog.presentation.notification.notificationRoutes
import io.paku.climblog.presentation.user.userRoutes
import io.paku.climblog.presentation.video.videoRoutes

fun Application.configureRouting() {
    val s3Bucket = environment.config.property("aws.s3Bucket").getString()
    val cloudFrontDomain = environment.config.property("aws.cloudFrontDomain").getString()

    routing {
        authRoutes()
        userRoutes()
        videoRoutes(s3Bucket, cloudFrontDomain)
        notificationRoutes()
    }
}
