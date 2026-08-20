package io.paku.climblog.data.database.table.user

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object UserFollowTable : Table("user_follows") {
    val followerId = reference("follower_id", UserTable,ReferenceOption.CASCADE)
    val followingId = reference("following_id", UserTable,ReferenceOption.CASCADE)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(followerId, followingId)
}
