package io.paku.climblog.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object UserTable : Table("users") {
    val id = long("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255).nullable()
    val name = varchar("name", 100)
    val handle = varchar("handle", 50).uniqueIndex().nullable()
    val age = integer("age").nullable()
    val height = integer("height").nullable()
    val armReach = integer("arm_reach").nullable()
    val gender = varchar("gender", 10).nullable()
    val profilePhotoUrl = varchar("profile_photo_url", 512).nullable()

    override val primaryKey = PrimaryKey(id)
}
