package io.paku.kmp_template.data

import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import io.paku.kmp_template.data.sercurity.JwtTokenProviderImpl.Companion.REFRESH_TOKEN_EXPIRATION_MS
import io.paku.kmp_template.domain.RefreshTokenRepository

@OptIn(ExperimentalLettuceCoroutinesApi::class)
internal class RefreshTokenRepositoryImpl(
    private val redisCommand: RedisCoroutinesCommands<String, String>
): RefreshTokenRepository {
    private fun getKey(userId: Long) = "refreshToken:$userId"

    override suspend fun save(userId: Long, refreshToken: String) {
        val key = getKey(userId)
        redisCommand.set(key, refreshToken)
        redisCommand.expire(key, REFRESH_TOKEN_EXPIRATION_MS / 1000L)
    }

    override suspend fun validate(
        userId: Long,
        refreshToken: String
    ): Boolean {
        val savedToken = redisCommand.get(getKey(userId)) ?: return false
        return savedToken == refreshToken
    }

    override suspend fun update(userId: Long, newRefreshToken: String) {
        save(userId, newRefreshToken)
    }

    override suspend fun delete(userId: Long) {
        redisCommand.del(getKey(userId))
    }
}