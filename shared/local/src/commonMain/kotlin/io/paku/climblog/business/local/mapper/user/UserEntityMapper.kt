package io.paku.climblog.business.local.mapper.user

import io.paku.climblog.business.data.model.user.UserData
import io.paku.climblog.business.local.model.user.UserEntity
import io.paku.climblog.core.BiMapper

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
