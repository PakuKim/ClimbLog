package io.paku.kmp_template.feature.user

import org.jetbrains.exposed.v1.core.Table

object UserEntity : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 50)
    val password = varchar("password", 64)
    val userName = varchar("userName", 50)

    override val primaryKey = PrimaryKey(id)
}