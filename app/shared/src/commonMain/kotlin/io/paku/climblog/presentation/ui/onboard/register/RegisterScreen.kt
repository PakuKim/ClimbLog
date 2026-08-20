package io.paku.climblog.presentation.ui.onboard.register

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
internal fun RegisterScreen(
    viewModel: RegisterViewModel
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()
    
    val imagePicker = rememberImagePicker { bytes ->
        viewModel.onEvent(RegisterViewModelEvent.OnProfileImagePicked(bytes))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필 완성", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
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
            // Profile Photo Picker (Placeholder)
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

            // Email (Disabled if social)
//            OutlinedTextField(
//                value = state.email,
//                onValueChange = { },
//                label = { Text("이메일") },
//                modifier = Modifier.fillMaxWidth(),
//                enabled = !state.isSocialUser,
//                readOnly = true
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(RegisterViewModelEvent.OnNameChanged(it)) },
                label = { Text("이름") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Handle (Unique ID)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.handle,
                    onValueChange = { viewModel.onEvent(RegisterViewModelEvent.OnHandleChanged(it)) },
                    label = { Text("사용자 아이디 (Handle)") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("영문, 숫자, 밑줄, 마침표") },
                    isError = state.isHandleChecked && !state.isHandleAvailable
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.onEvent(RegisterViewModelEvent.OnCheckHandle) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("중복확인")
                }
            }
            if (state.isHandleChecked) {
                Text(
                    text = if (state.isHandleAvailable) "사용 가능한 아이디입니다." else "이미 사용 중인 아이디입니다.",
                    color = if (state.isHandleAvailable) Color(0xFF4CAF50) else Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start).padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Physical Info Section
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.age,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(RegisterViewModelEvent.OnAgeChanged(it)) },
                    label = { Text("나이") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.width(16.dp))
                // Gender Selection (Simple Toggle for now)
                Box(modifier = Modifier.weight(1f).height(56.dp).align(Alignment.CenterVertically)) {
                    TextButton(
                        onClick = { viewModel.onEvent(RegisterViewModelEvent.OnGenderChanged(if (state.gender == "M") "F" else "M")) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text("성별: ${if (state.gender == "M") "남성" else "여성"}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.height,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(RegisterViewModelEvent.OnHeightChanged(it)) },
                    label = { Text("키 (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = state.armReach,
                    onValueChange = { if (it.length <= 3) viewModel.onEvent(RegisterViewModelEvent.OnArmReachChanged(it)) },
                    label = { Text("암리치 (cm)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Submit Button
            Button(
                onClick = { viewModel.onEvent(RegisterViewModelEvent.OnRegisterSubmit) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = state.isHandleAvailable && state.name.isNotBlank() && state.handle.isNotBlank()
            ) {
                Text("가입 완료", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
