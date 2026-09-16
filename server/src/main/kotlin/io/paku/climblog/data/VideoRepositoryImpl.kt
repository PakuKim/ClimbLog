package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.user.UserFollowTable
import io.paku.climblog.data.database.table.video.VideoCruxTable
import io.paku.climblog.data.database.table.video.VideoTable
import io.paku.climblog.domain.VideoRepository
import io.paku.climblog.domain.model.video.Video
import io.paku.climblog.domain.model.video.VideoCrux
import io.paku.climblog.domain.model.video.VideoStatus
import org.jetbrains.exposed.v1.core.Random
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.not
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class VideoRepositoryImpl : VideoRepository {

    private fun ResultRow.toDomainVideo(videoCruxes: List<VideoCrux>): Video = Video(
        id = this[VideoTable.id].value,
        userId = this[VideoTable.userId].value,
        title = this[VideoTable.title],
        description = this[VideoTable.description],
        hlsUrl = this[VideoTable.hlsUrl],
        thumbnailUrl = this[VideoTable.thumbnailUrl],
        status = this[VideoTable.status],
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
            it[status] = video.status
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

    override suspend fun updateStatus(id: Long, status: VideoStatus): Boolean = dbQuery {
        VideoTable.update({ VideoTable.id eq id }) {
            it[VideoTable.status] = status
        } > 0
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

    override suspend fun findAllPaged(
        userId: Long?,
        cursor: Long?,
        limit: Int,
        sortBy: String,
        orderBy: String
    ): List<Video> = dbQuery {
        var query = VideoTable.selectAll()
        
        if (userId != null) {
            query = query.where { VideoTable.userId eq userId }
        }
        
        if (cursor != null) {
            query = if (orderBy == "DESC") {
                query.where { VideoTable.id less cursor }
            } else {
                query.where { VideoTable.id greater cursor }
            }
        }
        
        val sortOrder = if (orderBy == "DESC") SortOrder.DESC else SortOrder.ASC
        val sortColumn = when (sortBy) {
            "CREATED_AT" -> VideoTable.createdAt
            else -> VideoTable.id
        }

        val videoRows = query.orderBy(sortColumn, sortOrder)
            .limit(limit)
            .toList()

        val videoIds = videoRows.map { it[VideoTable.id].value }
        val cruxesMap = getCruxesForVideos(videoIds)

        videoRows.map { it.toDomainVideo(cruxesMap[it[VideoTable.id].value] ?: emptyList()) }
    }

    override suspend fun findHomeFeed(
        currentUserId: Long,
        cursor: Long?,
        limit: Int
    ): List<Video> = dbQuery {
        val followingIds = UserFollowTable.selectAll()
            .where { UserFollowTable.followerId eq currentUserId }
            .map { it[UserFollowTable.followingId].value }

        if (followingIds.isEmpty()) {
            return@dbQuery findAllPaged(null, cursor, limit, "CREATED_AT", "DESC")
        }

        // HOME Feed logic with two-phase cursor:
        // Positive cursor (>0): Searching in Followed Videos
        // Negative cursor (<0): Searching in Other Videos (abs(cursor))
        
        val results = mutableListOf<Video>()
        
        if (cursor == null || cursor > 0) {
            val followedQuery = VideoTable.selectAll()
                .where { VideoTable.userId inList followingIds }
            
            val queryWithCursor = if (cursor != null) {
                followedQuery.where { VideoTable.id less cursor }
            } else {
                followedQuery
            }
            
            val followedVideos = queryWithCursor.orderBy(VideoTable.id to SortOrder.DESC)
                .limit(limit)
                .toList()
                .map { row -> 
                    val id = row[VideoTable.id].value
                    val cruxes = VideoCruxTable.selectAll().where { VideoCruxTable.videoId eq id }.map { it.toDomainCrux() }
                    row.toDomainVideo(cruxes)
                }
            
            results.addAll(followedVideos)
        }
        
        if (results.size < limit) {
            val remainingLimit = limit - results.size
            val othersCursor = if (cursor != null && cursor < 0) -cursor else null
            
            val othersQuery = VideoTable.selectAll()
                .where { not(VideoTable.userId inList followingIds) }
            
            val queryWithCursor = if (othersCursor != null) {
                othersQuery.where { VideoTable.id less othersCursor }
            } else {
                othersQuery
            }
            
            val otherVideos = queryWithCursor.orderBy(VideoTable.id to SortOrder.DESC)
                .limit(remainingLimit)
                .toList()
                .map { row ->
                    val id = row[VideoTable.id].value
                    val cruxes = VideoCruxTable.selectAll().where { VideoCruxTable.videoId eq id }.map { it.toDomainCrux() }
                    row.toDomainVideo(cruxes)
                }
            
            // Mark these videos with negative IDs in memory if needed for cursor, 
            // but we'll handle the nextCursor generation in the Route/UseCase.
            results.addAll(otherVideos)
        }
        
        results
    }

    override suspend fun findRandom(limit: Int, excludedIds: List<Long>): List<Video> = dbQuery {
        val query = if (excludedIds.isNotEmpty()) {
            VideoTable.selectAll().where { not(VideoTable.id inList excludedIds) }
        } else {
            VideoTable.selectAll()
        }
        
        val videoRows = query.orderBy(Random())
            .limit(limit)
            .toList()

        val videoIds = videoRows.map { it[VideoTable.id].value }
        val cruxesMap = getCruxesForVideos(videoIds)

        videoRows.map { it.toDomainVideo(cruxesMap[it[VideoTable.id].value] ?: emptyList()) }
    }
}
