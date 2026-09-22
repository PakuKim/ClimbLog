package io.paku.climblog.domain.interactors.user

import io.paku.climblog.domain.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() = userRepository.deleteUser()
}
