package io.paku.kmp_template.data

import io.paku.kmp_template.data.database.DatabaseFactory.dbQuery
import io.paku.kmp_template.data.database.table.UserTable
import io.paku.kmp_template.domain.UserRepository
import io.paku.kmp_template.domain.model.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update

internal class UserRepositoryImpl : UserRepository {
    // 나중에 MapperObject생성
    private fun ResultRow.toDomainUser(): User = User(
        id = this[UserTable.id],
        email = this[UserTable.email],
        passwordHash = this[UserTable.password],
        name = this[UserTable.name]
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

    override suspend fun findByEmail(email: String): User? = dbQuery {
        UserTable
            .selectAll()
            .where { UserTable.email eq email }
            .map {
                User(
                    id = it[UserTable.id],
                    email = it[UserTable.email],
                    passwordHash = it[UserTable.password],
                    name = it[UserTable.name]
                )
            }
            .singleOrNull()
    }

    override suspend fun save(user: User): User = dbQuery {
        UserTable.insert {
            it[email] = user.email
            it[password] = user.passwordHash
            it[name] = user.name
        }
        user
    }

    override suspend fun update(user: User): User = dbQuery {
        val updatedRows = UserTable.update({ UserTable.id eq user.id }) {
            it[email] = user.email
            it[password] = user.passwordHash
            it[name] = user.name
        }

        if (updatedRows == 0) {
            throw NoSuchElementException("User with id ${user.id} not found for update")
        }

        user
    }
}
