package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.VideoTable
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.Video
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class VideoRepositoryImpl : VideoRepository {

    private fun ResultRow.toDomainVideo(): Video = Video(
        id = this[VideoTable.id],
        userId = this[VideoTable.userId],
        title = this[VideoTable.title],
        description = this[VideoTable.description],
        hlsUrl = this[VideoTable.hlsUrl],
        thumbnailUrl = this[VideoTable.thumbnailUrl],
        cruxStartTime = this[VideoTable.cruxStartTime],
        cruxEndTime = this[VideoTable.cruxEndTime],
        createdAt = this[VideoTable.createdAt]
    )

    override suspend fun save(video: Video): Video = dbQuery {
        val insertedStatement = VideoTable.insert {
            it[userId] = video.userId
            it[title] = video.title
            it[description] = video.description
            it[hlsUrl] = video.hlsUrl
            it[thumbnailUrl] = video.thumbnailUrl
            it[cruxStartTime] = video.cruxStartTime
            it[cruxEndTime] = video.cruxEndTime
            it[createdAt] = video.createdAt
        }
        video.copy(id = insertedStatement[VideoTable.id])
    }

    override suspend fun findById(id: Long): Video? = dbQuery {
        VideoTable.selectAll()
            .where { VideoTable.id eq id }
            .map { it.toDomainVideo() }
            .singleOrNull()
    }

    override suspend fun findAllByUserId(userId: Long): List<Video> = dbQuery {
        VideoTable.selectAll()
            .where { VideoTable.userId eq userId }
            .map { it.toDomainVideo() }
    }

    override suspend fun findAllPaged(cursor: Long?, limit: Int): List<Video> = dbQuery {
        val query = if (cursor != null) {
            VideoTable.selectAll().where { VideoTable.id less cursor }
        } else {
            VideoTable.selectAll()
        }
        
        query.orderBy(VideoTable.id to SortOrder.DESC)
            .limit(limit)
            .map { it.toDomainVideo() }
    }

    override suspend fun findRandom(limit: Int): List<Video> = dbQuery {
        // Simple random: in real production we might use Native function 'RANDOM()'
        VideoTable.selectAll()
            .orderBy(org.jetbrains.exposed.v1.core.CustomFunction<Double>("RANDOM", org.jetbrains.exposed.v1.core.DoubleColumnType()))
            .limit(limit)
            .map { it.toDomainVideo() }
    }
}
