package io.paku.climblog.presentation.ui.upload

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.core.rememberVideoPicker
import io.paku.climblog.presentation.component.VideoPlayerView
import io.paku.climblog.presentation.component.rememberVideoPlayerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoUploadScreen(
    viewModel: VideoUploadViewModel,
    onNavigateBack: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()
    
    val videoPicker = rememberVideoPicker { videoFile ->
        viewModel.onEvent(VideoUploadEvent.OnVideoSelected(videoFile))
    }

    LaunchedEffect(state.uploadSuccess) {
        if (state.uploadSuccess) {
            onUploadSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("새 게시물", fontWeight = FontWeight.Bold) },
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
            // Video Picker Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable { videoPicker.pickVideo() },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedVideo?.previewUrl != null) {
                    val controller = rememberVideoPlayerController(state.selectedVideo.previewUrl)
                    VideoPlayerView(
                        controller = controller,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Overlay to allow re-selection
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Text("변경", color = Color.White, fontSize = 12.sp)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudUpload, 
                            contentDescription = null, 
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("클릭하여 영상 선택", fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metadata Fields
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(VideoUploadEvent.OnTitleChanged(it)) },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("오늘의 클라이밍 기록") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onEvent(VideoUploadEvent.OnDescriptionChanged(it)) },
                label = { Text("설명 (해시태그 포함)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                placeholder = { Text("#v6 #dyno #climbing") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "크럭스 구간 설정 (초)",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.cruxStartTime,
                    onValueChange = { viewModel.onEvent(VideoUploadEvent.OnCruxStartChanged(it)) },
                    label = { Text("시작") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("0.0") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = state.cruxEndTime,
                    onValueChange = { viewModel.onEvent(VideoUploadEvent.OnCruxEndChanged(it)) },
                    label = { Text("종료") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("10.0") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (state.uploadProgress > 0f && state.uploadProgress < 1f) {
                LinearProgressIndicator(
                    progress = { state.uploadProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
                Text("업로드 중... ${(state.uploadProgress * 100).toInt()}%", fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.onEvent(VideoUploadEvent.OnUploadClick) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.selectedVideo != null && state.title.isNotBlank() && !viewModel.isLoading.value,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("업로드 하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (state.errorMessage != null) {
                Text(state.errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
