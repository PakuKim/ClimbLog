package io.paku.climblog.business.data.mapper.user

import io.paku.climblog.business.data.model.user.UserData
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.core.BiMapper

internal object UserDataMapper : BiMapper<UserData, User> {
    override fun mapToRight(from: UserData): User {
        return User(
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

    override fun mapToLeft(from: User): UserData {
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
}
