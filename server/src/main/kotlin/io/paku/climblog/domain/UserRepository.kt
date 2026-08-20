package io.paku.climblog.domain

import io.paku.climblog.domain.model.user.User

interface UserRepository {
    suspend fun findById(id: Long): User?

    suspend fun findByHandle(handle: String): User?

    suspend fun findBySocialId(provider: String, providerId: String): User?

    suspend fun existsByHandle(handle: String): Boolean

    suspend fun search(query: String): List<User>

    suspend fun save(user: User): User

    suspend fun update(user: User): User

    suspend fun delete(id: Long)
}
