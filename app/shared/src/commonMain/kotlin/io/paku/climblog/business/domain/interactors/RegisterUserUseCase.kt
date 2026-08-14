package io.paku.climblog.business.domain.interactors

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.User

class RegisterUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        handle: String,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User> {
        return userRepository.registerUser(
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
    }
}
