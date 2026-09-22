package io.paku.climblog.domain.model.video

enum class VideoQuality(
    val maxId: Int,
    val width: Int,
    val height: Int,
    val maxFps: Int = 30
) {
    HIGH(1, 1920, 1080),
    STANDARD(2, 1280, 720),
    LOW(3, 854, 480)
}
