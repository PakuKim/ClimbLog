package io.paku.climblog.plugin

import io.ktor.server.application.Application
import io.paku.climblog.data.database.DatabaseFactory

fun Application.configureDatabases() {
    val dbDriver = environment.config.property("db.driver").getString()
    val dbUrl = environment.config.property("db.url").getString()
    
    // AWS RDS(PostgreSQL) might require user/password from env vars
    val dbUser = environment.config.propertyOrNull("db.user")?.getString() ?: ""
    val dbPassword = environment.config.propertyOrNull("db.password")?.getString() ?: ""

    DatabaseFactory.init(
        driver = dbDriver,
        url = dbUrl,
        user = dbUser,
        password = dbPassword
    )
}
