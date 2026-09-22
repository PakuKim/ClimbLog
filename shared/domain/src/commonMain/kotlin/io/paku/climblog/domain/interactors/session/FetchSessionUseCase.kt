package io.paku.climblog.domain.interactors.session

import kotlinx.coroutines.flow.Flow

class FetchSessionUseCase(
    private val repository: io.paku.climblog.domain.SessionRepository,
) {
    operator fun invoke(): Flow<Long?> {
        return repository.fetch()
    }
}