package io.paku.climblog.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.business.domain.model.video.VideoStatus
import kotlinx.datetime.LocalDateTime

@Composable
fun VideoThumbnailItem(
    video: Video,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f) // Instagram use 1:1 for grid
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
    ) {
        // TODO: Use Coil to load video.thumbnailUrl
        Text(
            "Thumbnail",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview
@Composable
private fun VideoThumbnailItemPreview() {
    PreviewWrapper {
        VideoThumbnailItem(
            video = Video(
                id = 1,
                userId = 1,
                title = "Sample Video",
                description = "This is a sample video description.",
                hlsUrl = "https://example.com/video.m3u8",
                thumbnailUrl = "https://example.com/thumbnail.jpg",
                status = VideoStatus.READY,
                cruxes = emptyList(),
                createdAt = LocalDateTime(2023, 1, 1, 0, 0)
            ),
            onClick = {}
        )
    }
}
