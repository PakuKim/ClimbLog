package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.user.UserTable
import io.paku.climblog.data.database.table.video.VideoCommentTable
import io.paku.climblog.domain.VideoCommentRepository
import io.paku.climblog.domain.model.video.VideoComment
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class VideoCommentRepositoryImpl : VideoCommentRepository {
    private fun ResultRow.toDomainComment(): VideoComment = VideoComment(
        id = this[VideoCommentTable.id].value,
        videoId = this[VideoCommentTable.videoId].value,
        userId = this[VideoCommentTable.userId].value,
        userName = this[UserTable.name],
        userProfilePhotoUrl = this[UserTable.profilePhotoUrl],
        content = this[VideoCommentTable.content],
        createdAt = this[VideoCommentTable.createdAt]
    )

    override suspend fun save(videoComment: VideoComment): VideoComment = dbQuery {
        val insertedStatement = VideoCommentTable.insert {
            it[videoId] = videoComment.videoId
            it[userId] = videoComment.userId
            it[content] = videoComment.content
        }

        (VideoCommentTable innerJoin UserTable)
            .selectAll()
            .where { VideoCommentTable.id eq insertedStatement[VideoCommentTable.id] }
            .map { it.toDomainComment() }
            .single()
    }

    override suspend fun findAllByVideoId(videoId: Long): List<VideoComment> = dbQuery {
        (VideoCommentTable innerJoin UserTable)
            .selectAll()
            .where { VideoCommentTable.videoId eq videoId }
            .orderBy(VideoCommentTable.createdAt, SortOrder.DESC)
            .map { it.toDomainComment() }
    }
}
