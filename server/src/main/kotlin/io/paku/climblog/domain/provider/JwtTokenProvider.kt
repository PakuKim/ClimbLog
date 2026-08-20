package io.paku.climblog.domain.provider

import com.auth0.jwt.JWTVerifier
import io.paku.climblog.domain.model.token.AuthToken

interface JwtTokenProvider {
    fun generateToken(userId: Long): AuthToken

    fun generateVerifier(): JWTVerifier

    fun verifyAndExtractUserId(token: String): Long?
}