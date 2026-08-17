package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: Long,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User> = runCatching {
        val user = userRepository.findById(userId) ?: throw Exception("User not found")
        val updatedUser = user.copy(
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
        userRepository.update(updatedUser)
    }
}
