package io.paku.climblog.data.database.table.video

import io.paku.climblog.data.database.table.user.UserTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object VideoLikeTable : Table("video_likes") {
    val videoId = reference("video_id", VideoTable,ReferenceOption.CASCADE)
    val userId = reference("user_id", UserTable,ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(videoId, userId)
}
