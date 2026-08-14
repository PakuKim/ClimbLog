package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.FollowTable
import io.paku.climblog.domain.FollowRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class FollowRepositoryImpl : FollowRepository {

    override suspend fun follow(followerId: Long, followingId: Long): Boolean = dbQuery {
        if (isFollowing(followerId, followingId)) return@dbQuery true
        FollowTable.insert {
            it[FollowTable.followerId] = followerId
            it[FollowTable.followingId] = followingId
            it[createdAt] = System.currentTimeMillis()
        }
        true
    }

    override suspend fun unfollow(followerId: Long, followingId: Long): Boolean = dbQuery {
        FollowTable.deleteWhere { (FollowTable.followerId eq followerId) and (FollowTable.followingId eq followingId) } > 0
    }

    override suspend fun isFollowing(followerId: Long, followingId: Long): Boolean = dbQuery {
        !FollowTable.selectAll()
            .where { (FollowTable.followerId eq followerId) and (FollowTable.followingId eq followingId) }
            .empty()
    }

    override suspend fun getFollowerCount(userId: Long): Long = dbQuery {
        FollowTable.selectAll()
            .where { FollowTable.followingId eq userId }
            .count()
    }

    override suspend fun getFollowingCount(userId: Long): Long = dbQuery {
        FollowTable.selectAll()
            .where { FollowTable.followerId eq userId }
            .count()
    }
}
