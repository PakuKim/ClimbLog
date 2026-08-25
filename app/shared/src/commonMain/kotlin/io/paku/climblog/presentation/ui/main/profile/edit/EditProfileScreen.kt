package io.paku.climblog.presentation.ui.main.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.core.rememberImagePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()
    
    val imagePicker = rememberImagePicker { bytes ->
        viewModel.onEvent(EditProfileViewModelEvent.OnProfileImageChanged(bytes))
    }

    LaunchedEffect(state.updateSuccess) {
        if (state.updateSuccess) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필 수정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
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
            // Profile Photo Picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { imagePicker.pickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (state.profileImageBytes != null) {
                    Text("이미지 선택됨", fontSize = 12.sp, color = Color(0xFF4CAF50))
                } else {
                    Text("사진 변경", fontSize = 12.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(EditProfileViewModelEvent.OnNameChanged(it)) },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Age & Gender
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.age,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(EditProfileViewModelEvent.OnAgeChanged(it)) },
                    label = { Text("나이") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f).height(56.dp).align(Alignment.CenterVertically)) {
                    TextButton(
                        onClick = { viewModel.onEvent(EditProfileViewModelEvent.OnGenderChanged(if (state.gender == "M") "F" else "M")) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("성별: ${if (state.gender == "M") "남성" else "여성"}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Height & Arm Reach
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.height,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(EditProfileViewModelEvent.OnHeightChanged(it)) },
                    label = { Text("키 (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = state.armReach,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(EditProfileViewModelEvent.OnArmReachChanged(it)) },
                    label = { Text("암리치 (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Submit Button
            Button(
                onClick = { viewModel.onEvent(EditProfileViewModelEvent.OnUpdateSubmit) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = state.name.isNotBlank() && !viewModel.isLoading.value
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("수정 완료", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
