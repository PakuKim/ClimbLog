package io.paku.climblog.data.database.table.notification

import io.paku.climblog.data.database.table.user.UserTable
import io.paku.climblog.data.database.table.video.VideoTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object NotificationTable : LongIdTable("notifications") {
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val type = varchar("type", 50)
    val fromUserId = reference("from_user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val videoId = reference("video_id", VideoTable, onDelete = ReferenceOption.CASCADE).nullable()
    val message = text("message")
    val isRead = bool("is_read").default(false)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}