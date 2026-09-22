package io.paku.climblog.data.mapper.user

import io.paku.climblog.core.BiMapper

internal object UserDataMapper : BiMapper<io.paku.climblog.data.model.user.UserData, io.paku.climblog.domain.model.user.User> {
    override fun mapToRight(from: io.paku.climblog.data.model.user.UserData): io.paku.climblog.domain.model.user.User {
        return _root_ide_package_.io.paku.climblog.domain.model.user.User(
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

    override fun mapToLeft(from: io.paku.climblog.domain.model.user.User): io.paku.climblog.data.model.user.UserData {
        return _root_ide_package_.io.paku.climblog.data.model.user.UserData(
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
