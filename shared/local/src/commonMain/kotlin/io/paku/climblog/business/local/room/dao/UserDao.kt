package io.paku.climblog.business.local.room.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import androidx.room3.Transaction
import io.paku.climblog.business.local.model.user.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Transaction
    @Query("SELECT * FROM user WHERE id = :id")
    fun fetchUser(id: Long): Flow<UserEntity?>

    @Insert(onConflict = REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("DELETE FROM user")
    suspend fun deleteAll()
}