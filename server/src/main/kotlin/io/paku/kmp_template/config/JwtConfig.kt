package io.paku.kmp_template.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val SECRET = "paku-secret" // Should be in env in prod
    private const val ISSUER = "io.paku.kmp-template"
    private const val AUDIENCE = "users"
    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000)) // 1 hour
            .sign(algorithm)
    }

    fun generateRefreshToken(email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + 86400000 * 30)) // 30 days
            .sign(algorithm)
    }

    val verifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()
}