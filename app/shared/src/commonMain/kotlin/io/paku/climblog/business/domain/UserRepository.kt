package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile

interface UserRepository {
    suspend fun getUser(): User
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<User>
    
    suspend fun getUserProfile(userId: Long): UserProfile

    suspend fun toggleFollow(userId: Long, isFollowing: Boolean)

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
