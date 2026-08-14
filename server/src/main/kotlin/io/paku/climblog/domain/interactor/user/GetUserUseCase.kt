package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<User> {
        val user = userRepository.findById(userId) ?: return Result.failure(IllegalArgumentException("User not found"))
        return Result.success(user)
    }
}