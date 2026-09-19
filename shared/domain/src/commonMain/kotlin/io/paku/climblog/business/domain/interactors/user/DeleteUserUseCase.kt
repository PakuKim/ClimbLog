package io.paku.climblog.business.domain.interactors.user

import io.paku.climblog.business.domain.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.deleteUser()
}
