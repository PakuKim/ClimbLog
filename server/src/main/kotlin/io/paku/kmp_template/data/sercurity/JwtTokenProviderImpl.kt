package io.paku.kmp_template.data.sercurity

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.paku.kmp_template.domain.model.AuthToken
import io.paku.kmp_template.domain.provider.JwtTokenProvider
import java.util.Date

internal class JwtTokenProviderImpl(
    secret: String,
    private val issuer: String,
    private val audience: String
) : JwtTokenProvider {
    private val algorithm = Algorithm.HMAC256(secret)

    override fun generateToken(
        userId: Long,
        email: String
    ): AuthToken {
        val accessToken = generateAccessToken(userId, email)
        val refreshToken = generateRefreshToken(userId, email)

        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun generateAccessToken(userId: Long, email: String): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1시간
            .sign(algorithm)
    }

    private fun generateRefreshToken(userId: Long, email: String): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000)) // 30일
            .sign(algorithm)
    }

    override fun generateVerifier(): JWTVerifier {
        return JWT
            .require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
}