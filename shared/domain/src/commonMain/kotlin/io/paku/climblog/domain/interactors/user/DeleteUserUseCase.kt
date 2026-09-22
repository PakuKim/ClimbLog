package io.paku.climblog.domain.interactors.user

class DeleteUserUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke() = userRepository.deleteUser()
}
