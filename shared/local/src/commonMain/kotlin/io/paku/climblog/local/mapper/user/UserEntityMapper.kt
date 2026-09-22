package io.paku.climblog.local.mapper.user

import io.paku.climblog.core.BiMapper
import io.paku.climblog.data.model.user.UserData
import io.paku.climblog.local.model.user.UserEntity

internal object UserEntityMapper : BiMapper<UserEntity, UserData> {
    override fun mapToRight(from: UserEntity): UserData {
        return UserData(
            id = from.id,
            name = from.name,
            handle = from.handle,
            age = from.age,
            height = from.height,
            armReach = from.armReach,
            gender = from.gender,
            profilePhotoUrl = from.profilePhotoUrl
        )
    }

    override fun mapToLeft(from: UserData): UserEntity {
        return UserEntity(
            id = from.id,
            name = from.name,
            handle = from.handle,
            age = from.age,
            height = from.height,
            armReach = from.armReach,
            gender = from.gender,
            profilePhotoUrl = from.profilePhotoUrl
        )
    }
}
