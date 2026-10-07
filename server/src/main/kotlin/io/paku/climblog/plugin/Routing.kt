package io.paku.climblog.plugin

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import io.paku.climblog.presentation.auth.authRoutes
import io.paku.climblog.presentation.notification.notificationRoutes
import io.paku.climblog.presentation.user.userRoutes
import io.paku.climblog.presentation.video.videoRoutes
import io.paku.climblog.presentation.video.workerRoutes

fun Application.configureRouting() {
    val s3Bucket = environment.config.propertyOrNull("r2.bucket")?.getString() ?: environment.config.property("aws.s3Bucket").getString()
    val cloudFrontDomain = environment.config.propertyOrNull("r2.publicBaseUrl")?.getString() ?: environment.config.property("aws.cloudFrontDomain").getString()
    val workerAuthToken = environment.config.propertyOrNull("worker.authToken")?.getString() ?: ""

    routing {
        authRoutes()
        userRoutes()
        videoRoutes(s3Bucket, cloudFrontDomain)
        notificationRoutes()
        workerRoutes(workerAuthToken)
    }
}
