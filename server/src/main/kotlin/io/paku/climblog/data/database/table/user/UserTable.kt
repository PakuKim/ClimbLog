package io.paku.climblog.data.database.table.user

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object UserTable : LongIdTable("users") {
    val name = varchar("name", 100)
    val handle = varchar("handle", 50).uniqueIndex()
    val age = integer("age")
    val height = integer("height")
    val armReach = integer("arm_reach")
    val gender = varchar("gender", 10)
    val profilePhotoUrl = varchar("profile_photo_url", 512).nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}
