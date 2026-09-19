package io.paku.climblog.presentation.ui.main.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.paku.climblog.presentation.component.SharedTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val state by viewModel.state

    LaunchedEffect(state.isLogoutSuccess) {
        if (state.isLogoutSuccess) {
            onLogoutSuccess()
        }
    }

    SettingsScreen(
        onNavigateBack = onNavigateBack,
        onLogoutClick = { viewModel.onEvent(SettingsViewModelEvent.OnLogoutClick) },
        onDeleteAccountClick = { viewModel.onEvent(SettingsViewModelEvent.OnDeleteAccountClick) }
    )
}

@Composable
private fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "설정",
                onNavClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SettingsItem(
                icon = Icons.Default.Logout,
                title = "로그아웃",
                onClick = onLogoutClick
            )
            
            HorizontalDivider()
            
            SettingsItem(
                icon = Icons.Default.Delete,
                title = "계정 탈퇴",
                titleColor = Color.Red,
                onClick = onDeleteAccountClick
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = titleColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = titleColor)
    }
}
