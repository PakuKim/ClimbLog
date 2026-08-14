package io.paku.climblog.data

import io.paku.climblog.data.database.DatabaseFactory.dbQuery
import io.paku.climblog.data.database.table.UserTable
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class UserRepositoryImpl : UserRepository {
    
    private fun ResultRow.toDomainUser(): User = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        passwordHash = this[UserTable.password],
        name = this[UserTable.name],
        handle = this[UserTable.handle],
        age = this[UserTable.age],
        height = this[UserTable.height],
        armReach = this[UserTable.armReach],
        gender = this[UserTable.gender],
        profilePhotoUrl = this[UserTable.profilePhotoUrl]
    )

    override suspend fun findById(id: Long): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.id eq id }
            .map { it.toDomainUser() }
            .singleOrNull()
    }

    override suspend fun existsByEmail(email: String): Boolean = dbQuery {
        !UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .empty()
    }

    override suspend fun existsByHandle(handle: String): Boolean = dbQuery {
        !UserTable
            .selectAll()
            .where { UserTable.handle eq handle }
            .empty()
    }

    override suspend fun findByEmail(email: String): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .map { it.toDomainUser() }
            .singleOrNull()
    }

    override suspend fun findByHandle(handle: String): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.handle eq handle }
            .map { it.toDomainUser() }
            .singleOrNull()
    }

    override suspend fun search(query: String): List<User> = dbQuery {
        UserTable.selectAll()
            .where { (UserTable.handle like "%$query%") or (UserTable.name like "%$query%") }
            .map { it.toDomainUser() }
    }

    override suspend fun save(user: User): User = dbQuery {
        val insertedStatement = UserTable.insert {
            it[email] = user.email
            it[password] = user.passwordHash
            it[name] = user.name
            it[handle] = user.handle
            it[age] = user.age
            it[height] = user.height
            it[armReach] = user.armReach
            it[gender] = user.gender
            it[profilePhotoUrl] = user.profilePhotoUrl
        }
        
        user.copy(id = insertedStatement[UserTable.id])
    }

    override suspend fun update(user: User): User = dbQuery {
        val updatedRows = UserTable.update({ UserTable.id eq user.id }) {
            it[email] = user.email
            it[password] = user.passwordHash
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

        user
    }
}
