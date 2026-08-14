package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.CommentTable
import io.paku.climblog.data.database.table.UserTable
import io.paku.climblog.domain.CommentRepository
import io.paku.climblog.domain.model.Comment
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class CommentRepositoryImpl : CommentRepository {

    private fun ResultRow.toDomainComment(): Comment = Comment(
        id = this[CommentTable.id],
        videoId = this[CommentTable.videoId],
        userId = this[CommentTable.userId],
        userName = this[UserTable.name],
        userProfilePhotoUrl = this[UserTable.profilePhotoUrl],
        content = this[CommentTable.content],
        createdAt = this[CommentTable.createdAt]
    )

    override suspend fun save(comment: Comment): Comment = dbQuery {
        val insertedStatement = CommentTable.insert {
            it[videoId] = comment.videoId
            it[userId] = comment.userId
            it[content] = comment.content
            it[createdAt] = System.currentTimeMillis()
        }
        
        // Fetch user info to return complete domain model
        (CommentTable innerJoin UserTable)
            .selectAll()
            .where { CommentTable.id eq insertedStatement[CommentTable.id] }
            .map { it.toDomainComment() }
            .single()
    }

    override suspend fun findAllByVideoId(videoId: Long): List<Comment> = dbQuery {
        (CommentTable innerJoin UserTable)
            .selectAll()
            .where { CommentTable.videoId eq videoId }
            .orderBy(CommentTable.createdAt, org.jetbrains.exposed.v1.core.SortOrder.DESC)
            .map { it.toDomainComment() }
    }
}
