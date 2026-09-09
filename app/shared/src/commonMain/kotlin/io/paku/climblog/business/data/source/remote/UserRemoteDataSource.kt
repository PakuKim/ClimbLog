package io.paku.climblog.business.data.source.remote

import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.user.UserProfile

interface UserRemoteDataSource {
    suspend fun getUser(): User
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<User>
    
    suspend fun getUserProfile(userId: Long): UserProfile
    
    suspend fun follow(userId: Long)
    suspend fun unfollow(userId: Long)
    suspend fun getFollowers(userId: Long): List<User>
    suspend fun getFollowing(userId: Long): List<User>
    suspend fun getFollowStatus(userId: Long): Boolean
    
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
