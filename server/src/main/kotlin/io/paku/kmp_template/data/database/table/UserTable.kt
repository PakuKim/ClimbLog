package io.paku.kmp_template.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object UserTable : Table("users") {
    val id = long("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val name = varchar("name", 100)

    override val primaryKey = PrimaryKey(id)
}