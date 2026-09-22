package io.paku.climblog.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

internal class UserRepositoryImpl(
    private val userRemoteDataSource: io.paku.climblog.data.source.remote.UserRemoteDataSource,
    private val userLocalDataSource: io.paku.climblog.data.source.local.UserLocalDataSource,
    private val sessionLocalDataSource: io.paku.climblog.data.source.local.SessionLocalDataSource,
): io.paku.climblog.domain.UserRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun fetchUserData(): Flow<io.paku.climblog.domain.model.user.User> {
        return sessionLocalDataSource.fetchUserId()
            .filterNotNull()
            .flatMapLatest {
                userLocalDataSource.fetchUser(it)
            }
            .map { io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it) }
    }

    override suspend fun getUser(): io.paku.climblog.domain.model.user.User {
        return userRemoteDataSource.getUser().let {
            sessionLocalDataSource.saveUserId(it.id)
            userLocalDataSource.saveUser(it)
            io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it)
        }
    }

    override suspend fun checkHandle(handle: String): Boolean {
        return userRemoteDataSource.checkHandle(handle)
    }

    override suspend fun searchUsers(query: String): List<io.paku.climblog.domain.model.user.User> {
        return userRemoteDataSource.searchUsers(query).map { io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it) }
    }

    override suspend fun getUserProfile(userId: Long): io.paku.climblog.domain.model.user.UserProfile {
        return userRemoteDataSource.getUserProfile(userId).let { profileData ->
            _root_ide_package_.io.paku.climblog.domain.model.user.UserProfile(
                user = io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(
                    profileData.user
                ),
                followerCount = profileData.followerCount,
                followingCount = profileData.followingCount,
                videoCount = profileData.videoCount,
                isFollowing = profileData.isFollowing
            )
        }
    }

    override suspend fun toggleFollow(userId: Long, isFollowing: Boolean) {
        if (isFollowing) {
            userRemoteDataSource.unfollow(userId)
        } else {
            userRemoteDataSource.follow(userId)
        }
    }

    override suspend fun getFollowers(userId: Long): Result<List<io.paku.climblog.domain.model.user.User>> = runCatching {
        userRemoteDataSource.getFollowers(userId).map { io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it) }
    }

    override suspend fun getFollowing(userId: Long): Result<List<io.paku.climblog.domain.model.user.User>> = runCatching {
        userRemoteDataSource.getFollowing(userId).map { io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it) }
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
    ): io.paku.climblog.domain.model.user.User {
        return userRemoteDataSource.updateUser(
            name = name,
            age = age,
            height = height,
            armReach = armReach,
            gender = gender,
            profilePhotoUrl = profilePhotoUrl
        ).let { io.paku.climblog.data.mapper.user.UserDataMapper.mapToRight(it) }
    }

    override suspend fun deleteUser() {
        userRemoteDataSource.deleteUser()
    }
}
