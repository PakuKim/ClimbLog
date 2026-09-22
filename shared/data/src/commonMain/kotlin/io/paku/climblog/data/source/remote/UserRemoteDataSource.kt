package io.paku.climblog.data.source.remote

import io.paku.climblog.data.model.user.UserData
import io.paku.climblog.data.model.user.UserProfileData

interface UserRemoteDataSource {
    suspend fun getUser(): UserData
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<UserData>
    
    suspend fun getUserProfile(userId: Long): UserProfileData
    
    suspend fun follow(userId: Long)
    suspend fun unfollow(userId: Long)
    suspend fun getFollowers(userId: Long): List<UserData>
    suspend fun getFollowing(userId: Long): List<UserData>
    suspend fun getFollowStatus(userId: Long): Boolean
    
    suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): UserData

    suspend fun deleteUser()
}
