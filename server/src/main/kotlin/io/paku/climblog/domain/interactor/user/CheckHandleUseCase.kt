package io.paku.climblog.domain.interactor.user

import io.paku.climblog.domain.UserRepository

internal class CheckHandleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(handle: String): Boolean {
        return userRepository.existsByHandle(handle)
    }
}
