package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class CheckHandleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(handle: String) = userRepository.checkHandle(handle)
}