package io.paku.climblog.presentation.ui.main.upload

import io.paku.climblog.domain.interactors.video.UploadVideoUseCase
import io.paku.climblog.domain.model.video.VideoQuality
import io.paku.climblog.platform.Media
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class VideoUploadViewModelState(
    val selectedMedia: Media.Video? = null,
    val selectedQuality: VideoQuality = VideoQuality.STANDARD,
    val isQualitySheetVisible: Boolean = false,
    val title: String = "",
    val description: String = "",
    val cruxStartTime: String = "",
    val cruxEndTime: String = "",
    val uploadProgress: Float = 0f,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
) : ViewModelState

sealed class VideoUploadViewModelEvent : ViewModelEvent {
    data class OnMediaSelected(val video: Media.Video?) : VideoUploadViewModelEvent()
    data class OnQualitySelected(val quality: VideoQuality) : VideoUploadViewModelEvent()
    data class SetQualitySheetVisible(val visible: Boolean) : VideoUploadViewModelEvent()
    data class OnTitleChanged(val title: String) : VideoUploadViewModelEvent()
    data class OnDescriptionChanged(val description: String) : VideoUploadViewModelEvent()
    data class OnCruxStartChanged(val time: String) : VideoUploadViewModelEvent()
    data class OnCruxEndChanged(val time: String) : VideoUploadViewModelEvent()
    object OnUploadClick : VideoUploadViewModelEvent()
}

internal class VideoUploadViewModel(
    private val uploadVideoUseCase: UploadVideoUseCase
) : BaseViewModel<VideoUploadViewModelState, VideoUploadViewModelEvent, Nothing>() {

    override fun createInitialState(): VideoUploadViewModelState = VideoUploadViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is VideoUploadViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: VideoUploadViewModelEvent) {
        when (event) {
            is VideoUploadViewModelEvent.OnMediaSelected -> updateState { copy(selectedMedia = event.video) }
            is VideoUploadViewModelEvent.OnQualitySelected -> updateState { copy(selectedQuality = event.quality, isQualitySheetVisible = false) }
            is VideoUploadViewModelEvent.SetQualitySheetVisible -> updateState { copy(isQualitySheetVisible = event.visible) }
            is VideoUploadViewModelEvent.OnTitleChanged -> updateState { copy(title = event.title) }
            is VideoUploadViewModelEvent.OnDescriptionChanged -> updateState { copy(description = event.description) }
            is VideoUploadViewModelEvent.OnCruxStartChanged -> updateState { copy(cruxStartTime = event.time) }
            is VideoUploadViewModelEvent.OnCruxEndChanged -> updateState { copy(cruxEndTime = event.time) }
            is VideoUploadViewModelEvent.OnUploadClick -> uploadVideo()
        }
    }

    private fun uploadVideo() = launch {
        val s = state.value
        val media = s.selectedMedia ?: return@launch
        
        setLoading(true)
        uploadVideoUseCase(
            title = s.title,
            description = s.description,
            media = media.source,
            quality = s.selectedQuality,
            cruxStartTime = s.cruxStartTime.toDoubleOrNull(),
            cruxEndTime = s.cruxEndTime.toDoubleOrNull(),
            onProgress = { progress ->
                updateState { copy(uploadProgress = progress) }
            }
        ).onSuccess {
            updateState { copy(uploadSuccess = true) }
            setLoading(false)
        }.onFailure { error ->
            updateState { copy(errorMessage = error.message ?: "Upload failed") }
            setLoading(false)
        }
    }
}
