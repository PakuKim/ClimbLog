package io.paku.climblog.domain.interactor.auth

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AuthToken
import io.paku.climblog.domain.model.User
import io.paku.climblog.domain.provider.JwtTokenProvider

data class SocialLoginResult(
    val tokens: AuthToken,
    val isRegistered: Boolean
)

class SocialLoginUseCase(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {
    suspend operator fun invoke(
        email: String,
        name: String,
        socialId: String,
        provider: String
    ): Result<SocialLoginResult> {
        var user = userRepository.findByEmail(email)
        
        if (user == null) {
            // Create a new user skeleton
            val newUser = User(
                email = email,
                name = name
            )
            user = userRepository.save(newUser)
        }
        
        val tokens = jwtTokenProvider.generateToken(user.id)
        val isRegistered = user.handle != null
        
        return Result.success(
            SocialLoginResult(
                tokens = tokens,
                isRegistered = isRegistered
            )
        )
    }
}
