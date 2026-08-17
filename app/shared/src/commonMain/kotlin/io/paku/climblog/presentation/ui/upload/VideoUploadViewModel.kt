package io.paku.climblog.presentation.ui.upload

import io.paku.climblog.business.domain.interactors.video.UploadVideoUseCase
import io.paku.climblog.core.VideoFile
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State

data class VideoUploadState(
    val selectedVideo: VideoFile? = null,
    val title: String = "",
    val description: String = "",
    val cruxStartTime: String = "",
    val cruxEndTime: String = "",
    val uploadProgress: Float = 0f,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
) : State

sealed class VideoUploadEvent : Event {
    data class OnVideoSelected(val video: VideoFile?) : VideoUploadEvent()
    data class OnTitleChanged(val title: String) : VideoUploadEvent()
    data class OnDescriptionChanged(val description: String) : VideoUploadEvent()
    data class OnCruxStartChanged(val time: String) : VideoUploadEvent()
    data class OnCruxEndChanged(val time: String) : VideoUploadEvent()
    object OnUploadClick : VideoUploadEvent()
}

class VideoUploadViewModel(
    private val uploadVideoUseCase: UploadVideoUseCase
) : BaseViewModel<VideoUploadState, VideoUploadEvent>() {

    override fun createInitialState(): VideoUploadState = VideoUploadState()

    override fun createTriggerEvent(event: Event) {
        if (event is VideoUploadEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: VideoUploadEvent) {
        when (event) {
            is VideoUploadEvent.OnVideoSelected -> updateState { copy(selectedVideo = event.video) }
            is VideoUploadEvent.OnTitleChanged -> updateState { copy(title = event.title) }
            is VideoUploadEvent.OnDescriptionChanged -> updateState { copy(description = event.description) }
            is VideoUploadEvent.OnCruxStartChanged -> updateState { copy(cruxStartTime = event.time) }
            is VideoUploadEvent.OnCruxEndChanged -> updateState { copy(cruxEndTime = event.time) }
            is VideoUploadEvent.OnUploadClick -> uploadVideo()
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
