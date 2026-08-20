package io.paku.climblog.domain.interactor.user

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.user.User

internal class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): User {
        val user = userRepository.findById(userId) ?: throw AppException(
            HttpStatusCode.NotFound,
            "User not found"
        )
        return user
    }
}