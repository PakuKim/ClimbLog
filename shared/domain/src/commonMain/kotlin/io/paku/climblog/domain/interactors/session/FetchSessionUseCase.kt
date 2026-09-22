package io.paku.climblog.domain.interactors.session

import io.paku.climblog.domain.SessionRepository
import kotlinx.coroutines.flow.Flow

class FetchSessionUseCase(
    private val repository: SessionRepository,
) {
    operator fun invoke(): Flow<Long?> {
        return repository.fetch()
    }
}