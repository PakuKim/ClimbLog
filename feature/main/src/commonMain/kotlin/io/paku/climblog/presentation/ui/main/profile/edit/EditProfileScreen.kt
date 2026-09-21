package io.paku.climblog.presentation.ui.main.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.paku.climblog.core.rememberImagePicker
import io.paku.climblog.presentation.component.SharedButton
import io.paku.climblog.presentation.component.SharedInputLayout
import io.paku.climblog.presentation.component.SharedTextField
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.ext.noRippleClickable
import io.paku.climblog.presentation.theme.AppComponentColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EditProfileRoute(
    viewModel: EditProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state
    val imagePicker = rememberImagePicker { bytes ->
        bytes?.let { viewModel.onEvent(EditProfileViewModelEvent.OnProfileImageChanged(it)) }
    }

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            onNavigateBack()
        }
    }

    EditProfileScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onProfileImageClick = { imagePicker.pickImage() },
        onNameChanged = { viewModel.onEvent(EditProfileViewModelEvent.OnNameChanged(it)) },
        onAgeChanged = { viewModel.onEvent(EditProfileViewModelEvent.OnAgeChanged(it)) },
        onHeightChanged = { viewModel.onEvent(EditProfileViewModelEvent.OnHeightChanged(it)) },
        onArmReachChanged = { viewModel.onEvent(EditProfileViewModelEvent.OnArmReachChanged(it)) },
        onGenderChanged = { viewModel.onEvent(EditProfileViewModelEvent.OnGenderChanged(it)) },
        onUpdateSubmit = { viewModel.onEvent(EditProfileViewModelEvent.OnUpdateSubmit) }
    )
}

@Composable
private fun EditProfileScreen(
    state: EditProfileViewModelState,
    onNavigateBack: () -> Unit,
    onProfileImageClick: () -> Unit,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (String) -> Unit,
    onHeightChanged: (String) -> Unit,
    onArmReachChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onUpdateSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "프로필 편집",
                onNavClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .noRippleClickable(onProfileImageClick),
                model = state.profileImageBytes ?: state.profilePhotoUrl,
                contentDescription = "Profile Image"
            )

            Spacer(modifier = Modifier.height(32.dp))

            SharedInputLayout {
                SharedTextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    placeholderText = "이름",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SharedInputLayout {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SharedTextField(
                        modifier = Modifier.weight(1f),
                        value = state.age,
                        onValueChange = onAgeChanged,
                        placeholderText = "나이(만)",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    TextButton(
                        modifier = Modifier.weight(1f),
                        onClick = { onGenderChanged(if (state.gender == "M") "F" else "M") },
                        colors = AppComponentColors.textButtonColors()
                    ) {
                        Text("성별: ${if (state.gender == "M") "남성" else "여성"}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            SharedInputLayout {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SharedTextField(
                        modifier = Modifier.weight(1f),
                        value = state.height,
                        onValueChange = onHeightChanged,
                        placeholderText = "키 (cm)",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    SharedTextField(
                        modifier = Modifier.weight(1f),
                        value = state.armReach,
                        onValueChange = onArmReachChanged,
                        placeholderText = "암리치 (cm)",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            SharedButton(
                modifier = Modifier.fillMaxWidth(),
                title = "변경 사항 저장",
                onClick = onUpdateSubmit
            )
        }
    }
}
