package io.paku.kmp_template.data.database

import io.paku.kmp_template.data.database.table.UserTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal object DatabaseFactory {
    fun init(
        driver: String,
        url: String
    ) {
        val database = Database.connect(
            url = url,
            driver = driver
        )

        transaction(database) {
            SchemaUtils.create(UserTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) { suspendTransaction { block() } }
    }
}