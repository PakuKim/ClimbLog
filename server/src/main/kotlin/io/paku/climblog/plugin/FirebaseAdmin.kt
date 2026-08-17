package io.paku.climblog.plugin

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.Application
import io.ktor.server.application.log
import java.io.FileInputStream

fun Application.configureFirebaseAdmin() {
    val serviceAccountPath = environment.config.propertyOrNull("firebase.serviceAccountPath")?.getString()
    
    if (serviceAccountPath != null) {
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(FileInputStream(serviceAccountPath)))
            .build()

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options)
        }
    } else {
        log.warn("Firebase service account path not provided. Push notifications will not work.")
    }
}
