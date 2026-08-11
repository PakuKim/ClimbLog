package io.paku.kmp_template.domain.provider

import com.auth0.jwt.JWTVerifier
import io.paku.kmp_template.domain.model.AuthToken

interface JwtTokenProvider {
    fun generateToken(userId: Long, email: String): AuthToken

    fun generateVerifier(): JWTVerifier
}
