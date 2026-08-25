package io.paku.climblog.presentation.ui.main.upload

import io.paku.climblog.business.domain.interactors.video.UploadVideoUseCase
import io.paku.climblog.core.VideoFile
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState

data class VideoUploadViewModelState(
    val selectedVideo: VideoFile? = null,
    val title: String = "",
    val description: String = "",
    val cruxStartTime: String = "",
    val cruxEndTime: String = "",
    val uploadProgress: Float = 0f,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
) : ViewModelState

sealed class VideoUploadViewModelEvent : ViewModelEvent {
    data class OnVideoSelected(val video: VideoFile?) : VideoUploadViewModelEvent()
    data class OnTitleChanged(val title: String) : VideoUploadViewModelEvent()
    data class OnDescriptionChanged(val description: String) : VideoUploadViewModelEvent()
    data class OnCruxStartChanged(val time: String) : VideoUploadViewModelEvent()
    data class OnCruxEndChanged(val time: String) : VideoUploadViewModelEvent()
    object OnUploadClick : VideoUploadViewModelEvent()
}

class VideoUploadViewModel(
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
            is VideoUploadViewModelEvent.OnVideoSelected -> updateState { copy(selectedVideo = event.video) }
            is VideoUploadViewModelEvent.OnTitleChanged -> updateState { copy(title = event.title) }
            is VideoUploadViewModelEvent.OnDescriptionChanged -> updateState { copy(description = event.description) }
            is VideoUploadViewModelEvent.OnCruxStartChanged -> updateState { copy(cruxStartTime = event.time) }
            is VideoUploadViewModelEvent.OnCruxEndChanged -> updateState { copy(cruxEndTime = event.time) }
            is VideoUploadViewModelEvent.OnUploadClick -> uploadVideo()
        }
    }

    private fun uploadVideo() = launch {
        val s = state.value
        val video = s.selectedVideo ?: return@launch
        
        setLoading(true)
        uploadVideoUseCase(
            title = s.title,
            description = s.description,
            fileName = video.name,
            contentType = video.contentType,
            bytes = video.bytes,
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
