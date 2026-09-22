package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ) = userRepository.updateUser(
        name = name,
        age = age,
        height = height,
        armReach = armReach,
        gender = gender,
        profilePhotoUrl = profilePhotoUrl
    )
}
