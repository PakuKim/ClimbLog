package io.paku.kmp_template

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.paku.kmp_template.db.DatabaseFactory
import io.paku.kmp_template.plugin.configureRouting
import io.paku.kmp_template.plugin.configureSecurity
import io.paku.kmp_template.plugin.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    configureSecurity()
    configureSerialization()
    configureRouting()
}