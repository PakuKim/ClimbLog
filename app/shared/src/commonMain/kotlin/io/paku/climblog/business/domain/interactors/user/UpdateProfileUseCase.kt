package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

internal class UpdateProfileUseCase(
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
