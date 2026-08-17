package io.paku.climblog.business.domain

import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.UserProfile

interface UserRepository {
    suspend fun getUser(): Result<User>
    
    suspend fun checkHandle(handle: String): Result<Boolean>
    
    suspend fun searchUsers(query: String): Result<List<User>>
    
    suspend fun getUserProfile(userId: Long): Result<UserProfile>
    
    suspend fun follow(userId: Long): Result<Unit>
    suspend fun unfollow(userId: Long): Result<Unit>
    
    suspend fun updateUser(
        name: String,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): Result<User>

    suspend fun deleteUser(): Result<Unit>
    
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
