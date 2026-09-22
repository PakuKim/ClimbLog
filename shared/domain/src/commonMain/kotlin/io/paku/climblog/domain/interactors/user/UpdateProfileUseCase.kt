package io.paku.climblog.domain.interactors.user

class UpdateProfileUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
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
