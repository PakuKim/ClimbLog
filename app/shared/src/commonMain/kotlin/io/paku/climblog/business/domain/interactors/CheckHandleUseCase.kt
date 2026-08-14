package io.paku.climblog.business.domain.interactors

import io.paku.climblog.business.domain.UserRepository

class CheckHandleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(handle: String): Result<Boolean> {
        return userRepository.checkHandle(handle)
    }
}
