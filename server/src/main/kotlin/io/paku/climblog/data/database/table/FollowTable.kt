package io.paku.climblog.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object FollowTable : Table("follows") {
    val followerId = long("follower_id").references(UserTable.id)
    val followingId = long("following_id").references(UserTable.id)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(followerId, followingId)
}
