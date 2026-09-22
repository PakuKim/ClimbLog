package io.paku.climblog.domain

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun fetchUserData(): Flow<io.paku.climblog.domain.model.user.User>

    suspend fun getUser(): io.paku.climblog.domain.model.user.User
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<io.paku.climblog.domain.model.user.User>
    
    suspend fun getUserProfile(userId: Long): io.paku.climblog.domain.model.user.UserProfile

    suspend fun toggleFollow(userId: Long, isFollowing: Boolean)
    
    suspend fun getFollowers(userId: Long): Result<List<io.paku.climblog.domain.model.user.User>>
    
    suspend fun getFollowing(userId: Long): Result<List<io.paku.climblog.domain.model.user.User>>
    
    suspend fun getFollowStatus(userId: Long): Result<Boolean>

    suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): io.paku.climblog.domain.model.user.User

    suspend fun deleteUser()
}
