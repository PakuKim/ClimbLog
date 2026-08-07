package io.paku.kmp_template.business.domain.interactors.session

import io.paku.kmp_template.business.domain.SessionRepository
import kotlinx.coroutines.flow.Flow

class FetchSessionUseCase(
    private val repository: SessionRepository,
) {
    operator fun invoke(): Flow<String?> {
        return repository.fetch()
    }
}