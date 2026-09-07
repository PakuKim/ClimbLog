package io.paku.climblog.presentation.ui.onboard.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.paku.climblog.core.Media
import io.paku.climblog.core.rememberGalleryManager
import io.paku.climblog.presentation.component.PreviewWrapper
import io.paku.climblog.presentation.component.SharedButton
import io.paku.climblog.presentation.component.SharedInputLayout
import io.paku.climblog.presentation.component.SharedTextField
import io.paku.climblog.presentation.component.SharedTopAppBar
import io.paku.climblog.presentation.ext.noRippleClickable
import io.paku.climblog.presentation.theme.AppComponentColors
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun RegisterRoute(
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state
    val galleryManager = rememberGalleryManager {
        when (it) {
            is Media.Image -> viewModel.onEvent(RegisterViewModelEvent.OnProfileImageChanged(it))
            else -> {}
        }
    }

    RegisterScreen(
        state = state,
        onProfileImageChanged = { galleryManager.launch() },
        onNameChanged = { viewModel.onEvent(RegisterViewModelEvent.OnNameChanged(it)) },
        onHandleChanged = { viewModel.onEvent(RegisterViewModelEvent.OnHandleChanged(it)) },
        onHandleCheckClick = { viewModel.onEvent(RegisterViewModelEvent.OnHandleCheckClick) },
        onAgeChanged = { viewModel.onEvent(RegisterViewModelEvent.OnAgeChanged(it)) },
        onGenderChanged = { viewModel.onEvent(RegisterViewModelEvent.OnGenderChanged(it)) },
        onHeightChanged = { viewModel.onEvent(RegisterViewModelEvent.OnHeightChanged(it)) },
        onArmReachChanged = { viewModel.onEvent(RegisterViewModelEvent.OnArmReachChanged(it)) },
        onRegisterClick = { viewModel.onEvent(RegisterViewModelEvent.OnRegisterClick) }
    )
}

@Composable
private fun RegisterScreen(
    state: RegisterViewModelState,
    onProfileImageChanged: () -> Unit = {},
    onNameChanged: (String) -> Unit = {},
    onHandleChanged: (String) -> Unit = {},
    onHandleCheckClick: () -> Unit = {},
    onAgeChanged: (String) -> Unit = {},
    onGenderChanged: (String) -> Unit = {},
    onHeightChanged: (String) -> Unit = {},
    onArmReachChanged: (String) -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "회원가입"
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
            var image: ByteArray? by remember { mutableStateOf(null) }
            LaunchedEffect(state.profileImage) {
                image = state.profileImage?.source?.readBytes()
            }

            AsyncImage(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .noRippleClickable(onProfileImageChanged),
                model = image,
                contentDescription = "Profile Image"
            )

            Spacer(modifier = Modifier.height(32.dp))

            SharedInputLayout {
                SharedTextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    placeholderText = "이름",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SharedInputLayout(
                guideText = when (state.handleChecked) {
                    true -> "사용 가능한 아이디입니다."
                    false -> "사용할 수 없는 아이디입니다."
                    else -> null
                },
                isValid = state.handleChecked == true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SharedTextField(
                        modifier = Modifier.weight(1f),
                        value = state.handle,
                        onValueChange = onHandleChanged,
                        placeholderText = "사용자 ID",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onHandleCheckClick() }
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    SharedButton(
                        onClick = onHandleCheckClick,
                        title = "중복확인",
                        contentPadding = PaddingValues(
                            vertical = 14.dp, horizontal = 24.dp
                        ),
                        enabled = state.handle.isNotBlank()
                    )
                }
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
                        onValueChange = { onHeightChanged(it) },
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
                        onValueChange = { onArmReachChanged(it) },
                        placeholderText = "암리치 (cm)",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onHandleCheckClick() }
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            SharedButton(
                modifier = Modifier.fillMaxWidth(),
                title = "가입 완료",
                onClick = onRegisterClick,
                enabled = state.registrationAvailable
            )
        }
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    PreviewWrapper {
        RegisterScreen(RegisterViewModelState())
    }
}