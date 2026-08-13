package io.paku.kmp_template

import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import io.paku.kmp_template.data.database.DatabaseFactory
import io.paku.kmp_template.plugin.configureDI
import io.paku.kmp_template.plugin.configureRouting
import io.paku.kmp_template.plugin.configureSecurity
import io.paku.kmp_template.plugin.configureSerialization

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    val dbDriver = environment.config.property("db.driver").getString()
    val dbUrl = environment.config.property("db.url").getString()
    DatabaseFactory.init(
        driver = dbDriver,
        url = dbUrl
    )

    configureDI()
    configureSecurity()
    configureSerialization()
    configureRouting()
}