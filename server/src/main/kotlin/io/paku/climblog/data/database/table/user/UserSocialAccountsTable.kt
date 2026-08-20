package io.paku.climblog.data.database.table.user

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime

internal object UserSocialAccountsTable : LongIdTable("user_social_accounts") {
    val userId = reference("user_id", UserTable,ReferenceOption.CASCADE)
    val provider = varchar("provider", 20)
    val providerId = varchar("provider_id", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)

    init {
        uniqueIndex("idx_provider_provider_id", provider, providerId)
    }
}
