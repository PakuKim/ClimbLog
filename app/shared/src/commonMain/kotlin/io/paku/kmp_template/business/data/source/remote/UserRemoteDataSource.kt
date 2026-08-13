package io.paku.kmp_template.business.data.source.remote

interface UserRemoteDataSource {
    suspend fun getUser(): Long
}