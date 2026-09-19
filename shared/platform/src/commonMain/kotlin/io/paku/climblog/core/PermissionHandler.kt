package io.paku.climblog.core

import androidx.compose.runtime.Composable
import io.paku.climblog.business.domain.model.permission.PermissionType

interface PermissionHandler {
    @Composable
    fun AskPermission(permission: PermissionType)

    @Composable
    fun isPermissionGranted(permission: PermissionType): Boolean

    @Composable
    fun LaunchSettings()
}