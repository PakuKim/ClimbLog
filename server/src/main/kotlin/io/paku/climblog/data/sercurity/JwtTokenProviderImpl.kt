package io.paku.climblog.data.sercurity

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.paku.climblog.domain.model.AuthToken
import io.paku.climblog.domain.provider.JwtTokenProvider
import java.util.Date

internal class JwtTokenProviderImpl(
    secret: String,
    private val issuer: String,
    private val audience: String
) : JwtTokenProvider {
    companion object {
        const val ACCESS_TOKEN_EXPIRATION_MS = 1 * 24 * 60 * 60 * 1000L // 1일
        const val REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000L // 7일
    }
    private val algorithm = Algorithm.HMAC256(secret)

    override fun generateToken(
        userId: Long,
    ): AuthToken {
        val accessToken = generateAccessToken(userId)
        val refreshToken = generateRefreshToken(userId)

        return AuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun generateAccessToken(userId: Long): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
//            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
            .sign(algorithm)
    }

    private fun generateRefreshToken(userId: Long): String {
        return JWT.create()
            .withSubject(userId.toString())
            .withIssuer(issuer)
            .withAudience(audience)
//            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
            .sign(algorithm)
    }

    override fun generateVerifier(): JWTVerifier {
        return JWT
            .require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }

    override fun verifyAndExtractUserId(token: String): Long? {
        return try {
            val verifier = generateVerifier()
            val decodedJWT = verifier.verify(token)
            decodedJWT.subject.toLongOrNull()
        } catch (e: Exception) {
            null
        }
    }
}