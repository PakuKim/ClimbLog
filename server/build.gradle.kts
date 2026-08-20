plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
}

group = "io.paku.climblog"
version = "1.0.0"
application {
    mainClass = "io.paku.climblog.ApplicationKt"
}

dependencies {
    api(project(":core"))
    implementation(libs.logback)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.reactive)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)

    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.negotiation)

    implementation(libs.h2)
    implementation(libs.jbcrypt)
    implementation(libs.lettuce)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.awsS3)
    implementation(libs.firebaseAdmin)

    testImplementation(libs.ktor.server.test.host.jvm)
    testImplementation(libs.kotlin.test.junit)
}
