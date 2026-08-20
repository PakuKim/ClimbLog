package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.user.UserFollowTable
import io.paku.climblog.domain.UserFollowRepository
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll

internal class UserFollowRepositoryImpl : UserFollowRepository {
    override suspend fun follow(followerId: Long, followingId: Long): Boolean = dbQuery {
        if (isFollowing(followerId, followingId)) return@dbQuery true
        UserFollowTable.insert {
            it[UserFollowTable.followerId] = followerId
            it[UserFollowTable.followingId] = followingId
        }
        true
    }

    override suspend fun unfollow(followerId: Long, followingId: Long): Boolean = dbQuery {
        UserFollowTable.deleteWhere { (UserFollowTable.followerId eq followerId) and (UserFollowTable.followingId eq followingId) } > 0
    }

    override suspend fun isFollowing(followerId: Long, followingId: Long): Boolean = dbQuery {
        !UserFollowTable.selectAll()
            .where { (UserFollowTable.followerId eq followerId) and (UserFollowTable.followingId eq followingId) }
            .empty()
    }

    override suspend fun getFollowerCount(userId: Long): Long = dbQuery {
        UserFollowTable.selectAll()
            .where { UserFollowTable.followingId eq userId }
            .count()
    }

    override suspend fun getFollowingCount(userId: Long): Long = dbQuery {
        UserFollowTable.selectAll()
            .where { UserFollowTable.followerId eq userId }
            .count()
    }
}
