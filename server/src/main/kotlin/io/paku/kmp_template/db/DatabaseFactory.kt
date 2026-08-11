package io.paku.kmp_template.db

import io.paku.kmp_template.feature.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal object DatabaseFactory {
    private const val DRIVER_CLASS_NAME = "org.h2.Driver"
    private const val DATABASE_URL = "jdbc:h2:file:./build/db"

    fun init() {
        val driverClassName = DRIVER_CLASS_NAME
        val jdbcURL = DATABASE_URL
        val database = Database.connect(jdbcURL, driverClassName)

        transaction(database) {
            SchemaUtils.create(UserEntity)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        withContext(Dispatchers.IO) { block() }
}