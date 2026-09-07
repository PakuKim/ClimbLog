package io.paku.climblog.presentation.ui.main.upload

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
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.VideoQuality
import io.paku.climblog.core.rememberVideoPicker
import io.paku.climblog.presentation.component.SharedTextField
import io.paku.climblog.presentation.component.SharedTopAppBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun VideoUploadRoute(
    viewModel: VideoUploadViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    val state by viewModel.state
    
    val videoPicker = rememberVideoPicker { video ->
        viewModel.onEvent(VideoUploadViewModelEvent.OnMediaSelected(video))
    }

    LaunchedEffect(state.uploadSuccess) {
        if (state.uploadSuccess) {
            onUploadSuccess()
        }
    }

    VideoUploadScreen(
        state = state,
        isLoading = viewModel.isLoading.value,
        onNavigateBack = onNavigateBack,
        onPickVideoClick = { videoPicker.pickVideo() },
        onQualityClick = { viewModel.onEvent(VideoUploadViewModelEvent.SetQualitySheetVisible(true)) },
        onQualitySelected = { viewModel.onEvent(VideoUploadViewModelEvent.OnQualitySelected(it)) },
        onDismissQualitySheet = { viewModel.onEvent(VideoUploadViewModelEvent.SetQualitySheetVisible(false)) },
        onTitleChanged = { viewModel.onEvent(VideoUploadViewModelEvent.OnTitleChanged(it)) },
        onDescriptionChanged = { viewModel.onEvent(VideoUploadViewModelEvent.OnDescriptionChanged(it)) },
        onCruxStartChanged = { viewModel.onEvent(VideoUploadViewModelEvent.OnCruxStartChanged(it)) },
        onCruxEndChanged = { viewModel.onEvent(VideoUploadViewModelEvent.OnCruxEndChanged(it)) },
        onUploadClick = { viewModel.onEvent(VideoUploadViewModelEvent.OnUploadClick) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoUploadScreen(
    state: VideoUploadViewModelState,
    isLoading: Boolean,
    onNavigateBack: () -> Unit,
    onPickVideoClick: () -> Unit,
    onQualityClick: () -> Unit,
    onQualitySelected: (VideoQuality) -> Unit,
    onDismissQualitySheet: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onCruxStartChanged: (String) -> Unit,
    onCruxEndChanged: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SharedTopAppBar(
                title = "새 게시물",
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
            // Video Picker Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable(onClick = onPickVideoClick),
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedMedia != null) {
                    // In real app, we need a way to get URL for preview
                    // For now, providing a placeholder if URL is not directly available
                    Text("영상 선택됨: ${state.selectedMedia.fileName}", color = Color.Gray)
                    
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

            // Quality Selection
            OutlinedCard(
                onClick = onQualityClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HighQuality, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("업로드 화질", fontSize = 12.sp, color = Color.Gray)
                        Text(state.selectedQuality.name, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("변경", color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Metadata Fields
            SharedTextField(
                value = state.title,
                onValueChange = onTitleChanged,
                placeholderText = "제목 (오늘의 클라이밍 기록)"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SharedTextField(
                value = state.description,
                onValueChange = onDescriptionChanged,
                placeholderText = "설명 (해시태그 포함)",
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "크럭스 구간 설정 (초)",
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                SharedTextField(
                    value = state.cruxStartTime,
                    onValueChange = onCruxStartChanged,
                    placeholderText = "시작 (0.0)",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SharedTextField(
                    value = state.cruxEndTime,
                    onValueChange = onCruxEndChanged,
                    placeholderText = "종료 (10.0)",
                    modifier = Modifier.weight(1f)
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
                onClick = onUploadClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = state.selectedMedia != null && state.title.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("업로드 하기", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            if (state.errorMessage != null) {
                Text(state.errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
            }
        }

        if (state.isQualitySheetVisible) {
            ModalBottomSheet(
                onDismissRequest = onDismissQualitySheet
            ) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                    Text("업로드 화질 선택", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
                    VideoQuality.entries.forEach { quality ->
                        ListItem(
                            headlineContent = { Text(quality.name) },
                            supportingContent = { Text("${quality.width}x${quality.height}, 30fps") },
                            trailingContent = {
                                RadioButton(
                                    selected = state.selectedQuality == quality,
                                    onClick = { onQualitySelected(quality) }
                                )
                            },
                            modifier = Modifier.clickable { onQualitySelected(quality) }
                        )
                    }
                }
            }
        }
    }
}
