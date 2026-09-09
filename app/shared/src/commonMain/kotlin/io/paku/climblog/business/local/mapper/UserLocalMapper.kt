package io.paku.climblog.business.local.mapper

import io.paku.climblog.business.common.BiMapper
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.local.room.entity.UserEntity

internal object UserLocalMapper: BiMapper<User, UserEntity> {
    override fun mapToRight(from: User): UserEntity {
        return from.let { user ->
            UserEntity(
                id = user.id,
                name = user.name,
                handle = user.handle,
                age = user.age,
                height = user.height,
                armReach = user.armReach,
                gender = user.gender,
                profilePhotoUrl = user.profilePhotoUrl
            )
        }
    }

    override fun mapToLeft(from: UserEntity): User {
        return from.let { user ->
            User(
                id = user.id,
                name = user.name,
                handle = user.handle,
                age = user.age,
                height = user.height,
                armReach = user.armReach,
                gender = user.gender,
                profilePhotoUrl = user.profilePhotoUrl
            )
        }
    }
}