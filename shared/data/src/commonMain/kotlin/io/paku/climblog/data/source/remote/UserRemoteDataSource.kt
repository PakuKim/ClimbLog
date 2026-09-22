package io.paku.climblog.data.source.remote

interface UserRemoteDataSource {
    suspend fun getUser(): io.paku.climblog.data.model.user.UserData
    
    suspend fun checkHandle(handle: String): Boolean
    
    suspend fun searchUsers(query: String): List<io.paku.climblog.data.model.user.UserData>
    
    suspend fun getUserProfile(userId: Long): io.paku.climblog.data.model.user.UserProfileData
    
    suspend fun follow(userId: Long)
    suspend fun unfollow(userId: Long)
    suspend fun getFollowers(userId: Long): List<io.paku.climblog.data.model.user.UserData>
    suspend fun getFollowing(userId: Long): List<io.paku.climblog.data.model.user.UserData>
    suspend fun getFollowStatus(userId: Long): Boolean
    
    suspend fun updateUser(
        name: String?,
        age: Int?,
        height: Int?,
        armReach: Int?,
        gender: String?,
        profilePhotoUrl: String?
    ): io.paku.climblog.data.model.user.UserData

    suspend fun deleteUser()
}
