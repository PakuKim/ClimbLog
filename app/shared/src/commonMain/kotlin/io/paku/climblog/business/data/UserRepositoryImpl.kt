package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.remote.UserRemoteDataSource
import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {
    override suspend fun getUser(): Result<User> = runCatching {
        userRemoteDataSource.getUser()
    }

    override suspend fun checkHandle(handle: String): Result<Boolean> = runCatching {
        userRemoteDataSource.checkHandle(handle)
    }

    override suspend fun searchUsers(query: String): Result<List<User>> = runCatching {
        userRemoteDataSource.searchUsers(query)
    }

    override suspend fun getUserProfile(userId: Long): Result<UserProfile> = runCatching {
        userRemoteDataSource.getUserProfile(userId)
    }

    override suspend fun toggleFollow(userId: Long): Result<Boolean> = runCatching {
        userRemoteDataSource.toggleFollow(userId)
    }

    override suspend fun registerUser(
        handle: String,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User> = runCatching {
        userRemoteDataSource.registerUser(
            handle = handle,
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
    }
}
