package io.paku.kmp_template.domain

import io.paku.kmp_template.domain.model.User

interface UserRepository {
    suspend fun findById(id: Long): User?

    suspend fun findByEmail(email: String): User?

    suspend fun existsByEmail(email: String): Boolean

    suspend fun save(user: User): User

    suspend fun update(user: User): User
}