package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.remote.UserRemoteDataSource
import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource
): UserRepository {
    override suspend fun getUser(): User {
        return userRemoteDataSource.getUser()
    }

    override suspend fun checkHandle(handle: String): Boolean {
        return userRemoteDataSource.checkHandle(handle)
    }

    override suspend fun searchUsers(query: String): List<User> {
        return userRemoteDataSource.searchUsers(query)
    }

    override suspend fun getUserProfile(userId: Long): UserProfile {
        return userRemoteDataSource.getUserProfile(userId)
    }

    override suspend fun toggleFollow(userId: Long, isFollowing: Boolean) {
        if (isFollowing) {
            userRemoteDataSource.unfollow(userId)
        } else {
            userRemoteDataSource.follow(userId)
        }
    }

    override suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): User {
        return userRemoteDataSource.updateUser(
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        )
    }

    override suspend fun deleteUser() {
        userRemoteDataSource.deleteUser()
    }
}
