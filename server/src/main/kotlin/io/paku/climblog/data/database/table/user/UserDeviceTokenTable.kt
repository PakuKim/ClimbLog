package io.paku.climblog.data.database.table.user

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object UserDeviceTokenTable : LongIdTable("user_device_tokens") {
    val userId = reference("user_id", UserTable, onDelete = ReferenceOption.CASCADE)
    val fcmToken = varchar("fcm_token", 512)
    val updatedAt = long("updated_at")
}