package io.paku.climblog.presentation.ui.main.search

import io.paku.climblog.business.domain.interactors.user.SearchUsersUseCase
import io.paku.climblog.business.domain.interactors.video.GetRandomVideosUseCase
import io.paku.climblog.business.domain.model.user.User
import io.paku.climblog.business.domain.model.video.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.ViewModelEvent
import io.paku.climblog.presentation.base.ViewModelState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class SearchViewModelState(
    val query: String = "",
    val randomVideos: List<Video> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val isSearching: Boolean = false
) : ViewModelState

sealed class SearchViewModelEvent : ViewModelEvent {
    data class OnQueryChanged(val query: String) : SearchViewModelEvent()
    object LoadRandomVideos : SearchViewModelEvent()
}

internal class SearchViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getRandomVideosUseCase: GetRandomVideosUseCase
) : BaseViewModel<SearchViewModelState, SearchViewModelEvent, Nothing>() {

    private var searchJob: Job? = null

    override fun createInitialState(): SearchViewModelState = SearchViewModelState()

    override fun createTriggerEvent(event: ViewModelEvent) {
        if (event is SearchViewModelEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: SearchViewModelEvent) {
        when (event) {
            is SearchViewModelEvent.OnQueryChanged -> {
                updateState { copy(query = event.query) }
                performSearch(event.query)
            }
            is SearchViewModelEvent.LoadRandomVideos -> loadRandomVideos()
        }
    }

    private fun loadRandomVideos() = launch {
        getRandomVideosUseCase(18).onSuccess { videos ->
            updateState { copy(randomVideos = videos) }
        }
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            updateState { copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = launch {
            delay(300) // Debounce
            updateState { copy(isSearching = true) }
            val users = searchUsersUseCase(query)
            updateState { copy(searchResults = users, isSearching = false) }
        }
    }

    init {
        loadRandomVideos()
    }
}
