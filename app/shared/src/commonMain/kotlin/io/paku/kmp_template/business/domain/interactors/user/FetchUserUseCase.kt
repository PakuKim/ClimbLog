package io.paku.kmp_template.business.domain.interactors.user

import io.paku.kmp_template.business.domain.UserRepository

class FetchUserUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.getUser()
}