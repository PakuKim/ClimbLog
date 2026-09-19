package io.paku.climblog.business.local.model.user

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("user")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "handle")
    val handle: String,
    @ColumnInfo(name = "age")
    val age: Int,
    @ColumnInfo(name = "height")
    val height: Int,
    @ColumnInfo(name = "arm_reach")
    val armReach: Int,
    @ColumnInfo(name = "gender")
    val gender: String,
    @ColumnInfo(name = "profile_photo_url")
    val profilePhotoUrl: String? = null
)
