package io.paku.kmp_template.feature.auth

import io.paku.kmp_template.config.JwtConfig
import io.paku.kmp_template.db.DatabaseFactory.dbQuery
import io.paku.kmp_template.feature.user.UserEntity
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select

class AuthService {
    suspend fun register(request: RegisterRequest) {
        dbQuery {
            UserEntity.insert {
                it[UserEntity.email] = request.email
                it[UserEntity.userName] = request.userName
                it[UserEntity.password] = request.password
            }
        }
    }

    suspend fun login(request: LoginRequest): AuthResponse? {
        val user = dbQuery {
            UserEntity.select(UserEntity.email)
                .where { (UserEntity.email eq request.email) and (UserEntity.password eq request.password) }
                .singleOrNull()
        }
        
        return if (user != null) {
            AuthResponse(
                accessToken = JwtConfig.generateToken(request.email),
                refreshToken = JwtConfig.generateRefreshToken(request.email)
            )
        } else null
    }
}
