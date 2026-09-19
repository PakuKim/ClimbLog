package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.user.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun fetchUserData(): Flow<User>

    suspend fun getUser(): User
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<User>
    
    suspend fun getUserProfile(userId: Long): UserProfile

    suspend fun toggleFollow(userId: Long, isFollowing: Boolean)
    
    suspend fun getFollowers(userId: Long): Result<List<User>>
    
    suspend fun getFollowing(userId: Long): Result<List<User>>
    
    suspend fun getFollowStatus(userId: Long): Result<Boolean>

    suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): User

    suspend fun deleteUser()
}
