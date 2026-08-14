package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.User

class CompleteRegistrationUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: Long,
        handle: String,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User> {
        val user = userRepository.findById(userId) ?: return Result.failure(Exception("User not found"))
        
        if (userRepository.existsByHandle(handle) && user.handle != handle) {
            return Result.failure(IllegalArgumentException("Handle already exists"))
        }

        val updatedUser = user.copy(
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )

        return Result.success(userRepository.update(updatedUser))
    }
}
