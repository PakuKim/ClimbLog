package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.NotificationTable
import io.paku.climblog.data.database.table.UserTable
import io.paku.climblog.domain.NotificationRepository
import io.paku.climblog.domain.model.Notification
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class NotificationRepositoryImpl : NotificationRepository {

    private fun ResultRow.toDomainNotification(): Notification = Notification(
        id = this[NotificationTable.id],
        userId = this[NotificationTable.userId],
        type = this[NotificationTable.type],
        fromUserId = this[NotificationTable.fromUserId],
        fromUserName = this[UserTable.name],
        fromUserProfilePhotoUrl = this[UserTable.profilePhotoUrl],
        videoId = this[NotificationTable.videoId],
        isRead = this[NotificationTable.isRead],
        createdAt = this[NotificationTable.createdAt]
    )

    override suspend fun findAllByUserId(userId: Long): List<Notification> = dbQuery {
        (NotificationTable innerJoin UserTable)
            .selectAll()
            .where { NotificationTable.userId eq userId }
            .orderBy(NotificationTable.createdAt, org.jetbrains.exposed.v1.core.SortOrder.DESC)
            .map { it.toDomainNotification() }
    }

    override suspend fun hasUnread(userId: Long): Boolean = dbQuery {
        !NotificationTable.selectAll()
            .where { (NotificationTable.userId eq userId) and (NotificationTable.isRead eq false) }
            .empty()
    }

    override suspend fun markAsRead(userId: Long) = dbQuery {
        NotificationTable.update({ NotificationTable.userId eq userId }) {
            it[isRead] = true
        }
        Unit
    }

    override suspend fun save(notification: Notification): Notification = dbQuery {
        val insertedStatement = NotificationTable.insert {
            it[userId] = notification.userId
            it[type] = notification.type
            it[fromUserId] = notification.fromUserId
            it[videoId] = notification.videoId
            it[isRead] = false
            it[createdAt] = System.currentTimeMillis()
        }
        notification.copy(id = insertedStatement[NotificationTable.id])
    }
}
