package io.paku.climblog.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object NotificationTable : Table("notifications") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val type = varchar("type", 50) // LIKE, COMMENT, FOLLOW
    val fromUserId = long("from_user_id").references(UserTable.id)
    val videoId = long("video_id").references(VideoTable.id).nullable()
    val isRead = bool("is_read").default(false)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
