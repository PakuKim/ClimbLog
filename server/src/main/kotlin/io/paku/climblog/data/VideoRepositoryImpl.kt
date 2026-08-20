package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.video.VideoCruxTable
import io.paku.climblog.data.database.table.video.VideoTable
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoCrux
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class VideoRepositoryImpl : VideoRepository {

    private fun ResultRow.toDomainVideo(videoCruxes: List<VideoCrux>): Video = Video(
        id = this[VideoTable.id].value,
        userId = this[VideoTable.userId].value,
        title = this[VideoTable.title],
        description = this[VideoTable.description],
        hlsUrl = this[VideoTable.hlsUrl],
        thumbnailUrl = this[VideoTable.thumbnailUrl],
        createdAt = this[VideoTable.createdAt],
        videoCruxes = videoCruxes
    )

    private fun ResultRow.toDomainCrux(): VideoCrux = VideoCrux(
        id = this[VideoCruxTable.id].value,
        videoId = this[VideoCruxTable.videoId].value,
        startTime = this[VideoCruxTable.cruxStartTime],
        endTime = this[VideoCruxTable.cruxEndTime]
    )

    private fun getCruxesForVideos(videoIds: List<Long>): Map<Long, List<VideoCrux>> {
        if (videoIds.isEmpty()) return emptyMap()
        
        return VideoCruxTable.selectAll()
            .where { 
                videoIds.map { id -> VideoCruxTable.videoId eq id }
                    .reduce { acc, op -> acc or op }
            }
            .map { it.toDomainCrux() }
            .groupBy { it.videoId }
    }

    override suspend fun save(video: Video): Video = dbQuery {
        val videoId = VideoTable.insert {
            it[userId] = video.userId
            it[title] = video.title
            it[description] = video.description
            it[hlsUrl] = video.hlsUrl
            it[thumbnailUrl] = video.thumbnailUrl
        }[VideoTable.id].value

        video.videoCruxes.forEach { crux ->
            VideoCruxTable.insert {
                it[VideoCruxTable.videoId] = videoId
                it[cruxStartTime] = crux.startTime
                it[cruxEndTime] = crux.endTime
            }
        }
        
        findById(videoId)!!
    }

    override suspend fun findById(id: Long): Video? = dbQuery {
        val videoRow = VideoTable.selectAll()
            .where { VideoTable.id eq id }
            .singleOrNull() ?: return@dbQuery null

        val cruxes = VideoCruxTable.selectAll()
            .where { VideoCruxTable.videoId eq id }
            .map { it.toDomainCrux() }

        videoRow.toDomainVideo(cruxes)
    }

    override suspend fun findAllByUserId(userId: Long): List<Video> = dbQuery {
        val videoRows = VideoTable.selectAll()
            .where { VideoTable.userId eq userId }
            .toList()
        
        val videoIds = videoRows.map { it[VideoTable.id].value }
        val cruxesMap = getCruxesForVideos(videoIds)

        videoRows.map { it.toDomainVideo(cruxesMap[it[VideoTable.id].value] ?: emptyList()) }
    }

    override suspend fun findAllPaged(cursor: Long?, limit: Int): List<Video> = dbQuery {
        val query = if (cursor != null) {
            VideoTable.selectAll().where { VideoTable.id less cursor }
        } else {
            VideoTable.selectAll()
        }
        
        val videoRows = query.orderBy(VideoTable.id to SortOrder.DESC)
            .limit(limit)
            .toList()

        val videoIds = videoRows.map { it[VideoTable.id].value }
        val cruxesMap = getCruxesForVideos(videoIds)

        videoRows.map { it.toDomainVideo(cruxesMap[it[VideoTable.id].value] ?: emptyList()) }
    }

    override suspend fun findRandom(limit: Int): List<Video> = dbQuery {
        val videoRows = VideoTable.selectAll()
            .orderBy(Random())
            .limit(limit)
            .toList()

        val videoIds = videoRows.map { it[VideoTable.id].value }
        val cruxesMap = getCruxesForVideos(videoIds)

        videoRows.map { it.toDomainVideo(cruxesMap[it[VideoTable.id].value] ?: emptyList()) }
    }
}
