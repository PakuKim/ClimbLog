package io.paku.climblog

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.paku.climblog.plugin.configureDI
import io.paku.climblog.plugin.configureDatabases
import io.paku.climblog.plugin.configureFirebaseAdmin
import io.paku.climblog.plugin.configureRouting
import io.paku.climblog.plugin.configureSecurity
import io.paku.climblog.plugin.configureSerialization

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureDatabases()
    configureFirebaseAdmin()
    configureDI()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
