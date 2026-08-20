package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

internal class CheckHandleUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(handle: String) = userRepository.checkHandle(handle)
}