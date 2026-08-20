package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.video.VideoLikeTable
import io.paku.climblog.domain.VideoLikeRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class VideoLikeRepositoryImpl : VideoLikeRepository {
    override suspend fun toggleLike(userId: Long, videoId: Long): Boolean = dbQuery {
        val exists = !VideoLikeTable.selectAll()
            .where { (VideoLikeTable.userId eq userId) and (VideoLikeTable.videoId eq videoId) }
            .empty()

        if (exists) {
            VideoLikeTable.deleteWhere { (VideoLikeTable.userId eq userId) and (VideoLikeTable.videoId eq videoId) }
            false
        } else {
            VideoLikeTable.insert {
                it[VideoLikeTable.userId] = userId
                it[VideoLikeTable.videoId] = videoId
            }
            true
        }
    }

    override suspend fun isLiked(userId: Long, videoId: Long): Boolean = dbQuery {
        !VideoLikeTable.selectAll()
            .where { (VideoLikeTable.userId eq userId) and (VideoLikeTable.videoId eq videoId) }
            .empty()
    }

    override suspend fun getLikeCount(videoId: Long): Long = dbQuery {
        VideoLikeTable.selectAll()
            .where { VideoLikeTable.videoId eq videoId }
            .count()
    }
}
