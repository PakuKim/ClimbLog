package io.paku.climblog.data.database.table

import org.jetbrains.exposed.v1.core.Table

internal object VideoTable : Table("videos") {
    val id = long("id").autoIncrement()
    val userId = long("user_id").references(UserTable.id)
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val hlsUrl = varchar("hls_url", 512)
    val thumbnailUrl = varchar("thumbnail_url", 512).nullable()
    val cruxStartTime = double("crux_start_time").nullable()
    val cruxEndTime = double("crux_end_time").nullable()
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}
