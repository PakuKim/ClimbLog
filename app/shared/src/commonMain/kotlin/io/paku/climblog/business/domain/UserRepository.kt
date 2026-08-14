package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile

interface UserRepository {
    suspend fun getUser(): Result<User>
    
    suspend fun checkHandle(handle: String): Result<Boolean>
    
    suspend fun searchUsers(query: String): Result<List<User>>
    
    suspend fun getUserProfile(userId: Long): Result<UserProfile>
    
    suspend fun toggleFollow(userId: Long): Result<Boolean>
    
    suspend fun registerUser(
        handle: String,
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User>
}
