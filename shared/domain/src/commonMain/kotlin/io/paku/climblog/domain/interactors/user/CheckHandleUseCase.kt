package io.paku.climblog.domain.interactors.user

class CheckHandleUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(handle: String) = userRepository.checkHandle(handle)
}