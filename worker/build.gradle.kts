plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    application
}

group = "io.paku.climblog"
version = "1.0.0"

application {
    mainClass.set("io.paku.climblog.worker.WorkerMainKt")
}

dependencies {
    implementation(project(":shared:contract"))
    implementation(libs.logback)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.awsS3)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
}
