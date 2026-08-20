package io.paku.climblog.data.database

import io.paku.climblog.data.database.table.notification.NotificationTable
import io.paku.climblog.data.database.table.user.UserDeviceTokenTable
import io.paku.climblog.data.database.table.user.UserFollowTable
import io.paku.climblog.data.database.table.user.UserSocialAccountsTable
import io.paku.climblog.data.database.table.user.UserTable
import io.paku.climblog.data.database.table.video.VideoCommentTable
import io.paku.climblog.data.database.table.video.VideoCruxTable
import io.paku.climblog.data.database.table.video.VideoLikeTable
import io.paku.climblog.data.database.table.video.VideoTable
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
                UserSocialAccountsTable,
                UserDeviceTokenTable,
                UserFollowTable,
                VideoTable,
                VideoCommentTable,
                VideoLikeTable,
                VideoCruxTable,
                NotificationTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) { suspendTransaction { block() } }
    }
}
