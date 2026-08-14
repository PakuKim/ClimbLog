package io.paku.climblog.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object CommentTable : Table("comments") {
    val id = long("id").autoIncrement()
    val videoId = long("video_id").references(VideoTable.id)
    val userId = long("user_id").references(UserTable.id)
    val content = text("content")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
