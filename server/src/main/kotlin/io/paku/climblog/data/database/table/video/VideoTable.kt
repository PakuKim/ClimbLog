package io.paku.climblog.data.database.table.video

import io.paku.climblog.data.database.table.user.UserTable
import io.paku.climblog.domain.model.video.VideoStatus
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object VideoTable : LongIdTable("videos") {
    val userId = reference("user_id", UserTable, ReferenceOption.CASCADE)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val hlsUrl = varchar("hls_url", 512).nullable()
    val thumbnailUrl = varchar("thumbnail_url", 512).nullable()
    val status = enumerationByName("status", 20, VideoStatus::class).default(VideoStatus.UPLOADING)
    val jobId = varchar("job_id", 128).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
