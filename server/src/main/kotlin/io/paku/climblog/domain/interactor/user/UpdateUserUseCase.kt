package io.paku.climblog.domain.interactor.user

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.user.User

internal class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: Long,
        name: String? = null,
        age: Int? = null,
        height: Int? = null,
        armReach: Int? = null,
        gender: String? = null,
        profilePhotoUrl: String?
    ): User {
        val user = userRepository.findById(userId) ?: throw AppException(HttpStatusCode.NotFound, "User not found")
        val updatedUser = user.copy(
            name = name ?: user.name,
            age = age ?: user.age,
            height = height ?: user.height,
            armReach = armReach ?: user.armReach,
            gender = gender ?: user.gender,
            profilePhotoUrl = profilePhotoUrl
        )
        return userRepository.update(updatedUser)
    }
}
