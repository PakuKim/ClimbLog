package io.paku.climblog.domain.interactor.auth

import io.ktor.http.HttpStatusCode
import io.paku.climblog.domain.UserRepository
import io.paku.climblog.domain.model.AppException
import io.paku.climblog.domain.model.user.User

internal class SocialRegisterUseCase(
    private val userRepository: UserRepository,
    private val verifySocialTokenUseCase: VerifySocialTokenUseCase
) {
    suspend operator fun invoke(
        provider: String,
        socialToken: String,
        handle: String,
        name: String,
        age: Int,
        height: Int,
        armReach: Int,
        gender: String,
        profilePhotoUrl: String?
    ) {
        val providerId = verifySocialTokenUseCase(provider, socialToken)
        if (userRepository.findBySocialId(provider, providerId) != null) {
            throw AppException(HttpStatusCode.Conflict, "User already registered with this social account")
        }

        if (userRepository.existsByHandle(handle)) {
            throw AppException(HttpStatusCode.Conflict, "Handle already exists")
        }

        val newUser = User(
            name = name,
            handle = handle,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl,
            social = mapOf(provider to providerId)
        )

        userRepository.save(newUser)
    }
}
