package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.user.UserSocialAccountsTable
import io.paku.climblog.data.database.table.user.UserTable
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.user.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class UserRepositoryImpl: UserRepository {
    private fun ResultRow.toDomainUser(social: Map<String, String>): User = User(
        id = this[UserTable.id].value,
        name = this[UserTable.name],
        handle = this[UserTable.handle],
        age = this[UserTable.age],
        height = this[UserTable.height],
        armReach = this[UserTable.armReach],
        gender = this[UserTable.gender],
        profilePhotoUrl = this[UserTable.profilePhotoUrl],
        social = social
    )

    private fun getSocialAccountsForUsers(userIds: List<Long>): Map<Long, Map<String, String>> {
        if (userIds.isEmpty()) return emptyMap()
        
        return UserSocialAccountsTable
            .selectAll()
            .where { 
                userIds.map { id -> UserSocialAccountsTable.userId eq id }
                    .reduce { acc, op -> acc or op }
            }
            .groupBy { it[UserSocialAccountsTable.userId].value }
            .mapValues { (_, rows) ->
                rows.associate { it[UserSocialAccountsTable.provider] to it[UserSocialAccountsTable.providerId] }
            }
    }

    override suspend fun findById(id: Long): User? = dbQuery {
        val userRow = UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .singleOrNull() ?: return@dbQuery null
        
        val socialMap = UserSocialAccountsTable
            .selectAll()
            .where { UserSocialAccountsTable.userId eq id }
            .associate { it[UserSocialAccountsTable.provider] to it[UserSocialAccountsTable.providerId] }
            
        userRow.toDomainUser(socialMap)
    }

    override suspend fun findByHandle(handle: String): User? = dbQuery {
        val userRow = UserTable
            .selectAll()
            .where { UserTable.handle eq handle }
            .singleOrNull() ?: return@dbQuery null
        
        val userId = userRow[UserTable.id].value
        val socialMap = UserSocialAccountsTable
            .selectAll()
            .where { UserSocialAccountsTable.userId eq userId }
            .associate { it[UserSocialAccountsTable.provider] to it[UserSocialAccountsTable.providerId] }
            
        userRow.toDomainUser(socialMap)
    }

    override suspend fun findBySocialId(provider: String, providerId: String): User? = dbQuery {
        val userId = UserSocialAccountsTable
            .selectAll()
            .where { (UserSocialAccountsTable.provider eq provider) and (UserSocialAccountsTable.providerId eq providerId) }
            .map { it[UserSocialAccountsTable.userId].value }
            .singleOrNull() ?: return@dbQuery null

        findById(userId)
    }

    override suspend fun existsByHandle(handle: String): Boolean = dbQuery {
        !UserTable
            .selectAll()
            .where { UserTable.handle eq handle }
            .empty()
    }

    override suspend fun search(query: String): List<User> = dbQuery {
        val userRows = UserTable
            .selectAll()
            .where { (UserTable.handle like "%$query%") or (UserTable.name like "%$query%") }
            .toList()
        
        val userIds = userRows.map { it[UserTable.id].value }
        val socialMapByUserId = getSocialAccountsForUsers(userIds)

        userRows.map { row ->
            row.toDomainUser(socialMapByUserId[row[UserTable.id].value] ?: emptyMap())
        }
    }

    override suspend fun save(user: User): User = dbQuery {
        val userId = UserTable.insert {
            it[name] = user.name
            it[handle] = user.handle
            it[age] = user.age
            it[height] = user.height
            it[armReach] = user.armReach
            it[gender] = user.gender
            it[profilePhotoUrl] = user.profilePhotoUrl
        }[UserTable.id].value

        user.social.forEach { (provider, providerId) ->
            UserSocialAccountsTable.insert {
                it[UserSocialAccountsTable.userId] = userId
                it[UserSocialAccountsTable.provider] = provider
                it[UserSocialAccountsTable.providerId] = providerId
            }
        }
        
        user.copy(id = userId)
    }

    override suspend fun update(user: User): User = dbQuery {
        val updatedRows = UserTable.update({ UserTable.id eq user.id }) {
            it[name] = user.name
            it[handle] = user.handle
            it[age] = user.age
            it[height] = user.height
            it[armReach] = user.armReach
            it[gender] = user.gender
            it[profilePhotoUrl] = user.profilePhotoUrl
        }

        if (updatedRows == 0) {
            throw NoSuchElementException("User with id ${user.id} not found for update")
        }

        // Sync social accounts
        UserSocialAccountsTable.deleteWhere { UserSocialAccountsTable.userId eq user.id }
        user.social.forEach { (provider, providerId) ->
            UserSocialAccountsTable.insert {
                it[UserSocialAccountsTable.userId] = user.id
                it[UserSocialAccountsTable.provider] = provider
                it[UserSocialAccountsTable.providerId] = providerId
            }
        }

        user
    }

    override suspend fun delete(id: Long) = dbQuery {
        UserTable.deleteWhere { UserTable.id eq id }
        Unit
    }
}
