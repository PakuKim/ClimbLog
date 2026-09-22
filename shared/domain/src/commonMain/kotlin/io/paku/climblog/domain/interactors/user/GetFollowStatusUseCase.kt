package io.paku.climblog.domain.interactors.user

class GetFollowStatusUseCase(
    private val userRepository: io.paku.climblog.domain.UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<Boolean> {
        return userRepository.getFollowStatus(userId)
    }
}
