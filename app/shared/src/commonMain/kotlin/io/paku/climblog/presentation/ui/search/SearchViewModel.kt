package io.paku.climblog.presentation.ui.search

import io.paku.climblog.business.domain.UserRepository
import io.paku.climblog.business.domain.VideoRepository
import io.paku.climblog.business.domain.model.User
import io.paku.climblog.business.domain.model.Video
import io.paku.climblog.presentation.base.BaseViewModel
import io.paku.climblog.presentation.base.Event
import io.paku.climblog.presentation.base.State
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class SearchState(
    val query: String = "",
    val randomVideos: List<Video> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val isSearching: Boolean = false
) : State

sealed class SearchEvent : Event {
    data class OnQueryChanged(val query: String) : SearchEvent()
    object LoadRandomVideos : SearchEvent()
}

internal class SearchViewModel(
    private val userRepository: UserRepository,
    private val videoRepository: VideoRepository
) : BaseViewModel<SearchState, SearchEvent>() {

    private var searchJob: Job? = null

    override fun createInitialState(): SearchState = SearchState()

    override fun createTriggerEvent(event: Event) {
        if (event is SearchEvent) {
            onEvent(event)
        }
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.OnQueryChanged -> {
                updateState { copy(query = event.query) }
                performSearch(event.query)
            }
            is SearchEvent.LoadRandomVideos -> loadRandomVideos()
        }
    }

    private fun loadRandomVideos() = launch {
        videoRepository.getRandomVideos(18).onSuccess { videos ->
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
            val users = userRepository.searchUsers(query)
            updateState { copy(searchResults = users, isSearching = false) }
        }
    }

    init {
        loadRandomVideos()
    }
}
