package io.paku.climblog.business.data

import io.paku.climblog.business.data.source.local.SessionLocalDataSource
import io.paku.climblog.business.data.source.local.UserLocalDataSource
import io.paku.climblog.business.data.source.remote.UserRemoteDataSource
import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.user.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

internal class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val sessionLocalDataSource: SessionLocalDataSource,
): UserRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchUserData(): Flow<User> {
        return sessionLocalDataSource.fetchUserId()
            .filterNotNull()
            .flatMapLatest {
                userLocalDataSource.fetchUser(it)
            }
    }

    override suspend fun getUser(): User {
        return userRemoteDataSource.getUser().also {
            sessionLocalDataSource.saveUserId(it.id)
            userLocalDataSource.saveUser(it)
        }
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

    override suspend fun getFollowers(userId: Long): Result<List<User>> = runCatching {
        userRemoteDataSource.getFollowers(userId)
    }

    override suspend fun getFollowing(userId: Long): Result<List<User>> = runCatching {
        userRemoteDataSource.getFollowing(userId)
    }

    override suspend fun getFollowStatus(userId: Long): Result<Boolean> = runCatching {
        userRemoteDataSource.getFollowStatus(userId)
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
