package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.LikeTable
import io.paku.climblog.domain.LikeRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class LikeRepositoryImpl : LikeRepository {

    override suspend fun toggleLike(userId: Long, videoId: Long): Boolean = dbQuery {
        val exists = !LikeTable.selectAll()
            .where { (LikeTable.userId eq userId) and (LikeTable.videoId eq videoId) }
            .empty()

        if (exists) {
            LikeTable.deleteWhere { (LikeTable.userId eq userId) and (LikeTable.videoId eq videoId) }
            false
        } else {
            LikeTable.insert {
                it[LikeTable.userId] = userId
                it[LikeTable.videoId] = videoId
                it[createdAt] = System.currentTimeMillis()
            }
            true
        }
    }

    override suspend fun isLiked(userId: Long, videoId: Long): Boolean = dbQuery {
        !LikeTable.selectAll()
            .where { (LikeTable.userId eq userId) and (LikeTable.videoId eq videoId) }
            .empty()
    }

    override suspend fun getLikeCount(videoId: Long): Long = dbQuery {
        LikeTable.selectAll()
            .where { LikeTable.videoId eq videoId }
            .count()
    }
}
