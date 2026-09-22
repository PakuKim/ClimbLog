package io.paku.climblog.platform

import androidx.compose.runtime.Composable
import io.paku.climblog.domain.model.permission.PermissionType

interface PermissionHandler {
    @Composable
    fun AskPermission(permission: PermissionType)

    @Composable
    fun isPermissionGranted(permission: PermissionType): Boolean

    @Composable
    fun LaunchSettings()
}