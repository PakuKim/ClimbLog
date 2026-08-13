package io.paku.kmp_template.business.domain

interface UserRepository {
    suspend fun getUser()
}