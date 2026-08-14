package io.paku.climblog.data.database

import io.paku.climblog.data.database.table.CommentTable
import io.paku.climblog.data.database.table.FollowTable
import io.paku.climblog.data.database.table.LikeTable
import io.paku.climblog.data.database.table.NotificationTable
import io.paku.climblog.data.database.table.UserTable
import io.paku.climblog.data.database.table.VideoTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

internal object DatabaseFactory {
    fun init(
        driver: String,
        url: String,
        user: String = "",
        password: String = ""
    ) {
        val database = Database.connect(
            url = url,
            driver = driver,
            user = user,
            password = password
        )

        transaction(database) {
            SchemaUtils.create(
                UserTable,
                VideoTable,
                CommentTable,
                LikeTable,
                FollowTable,
                NotificationTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) { suspendTransaction { block() } }
    }
}
