package io.paku.climblog.domain

import io.paku.climblog.domain.model.User

interface UserRepository {
    suspend fun findById(id: Long): User?

    suspend fun findByEmail(email: String): User?

    suspend fun findByHandle(handle: String): User?

    suspend fun existsByEmail(email: String): Boolean

    suspend fun existsByHandle(handle: String): Boolean

    suspend fun search(query: String): List<User>

    suspend fun save(user: User): User

    suspend fun update(user: User): User
}
