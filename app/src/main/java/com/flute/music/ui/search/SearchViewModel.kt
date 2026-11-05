package com.flute.music.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flute.music.data.model.Song
import com.flute.music.data.repository.LocalMusicRepository
import com.flute.music.data.repository.OnlineMusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val localResults: List<Song> = emptyList(),
    val onlineResults: List<Song> = emptyList(),
    val selectedGenre: String? = null,
    val genreResults: List<Song> = emptyList(),
    val isSearching: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val localMusicRepository: LocalMusicRepository,
    private val onlineMusicRepository: OnlineMusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val genres = listOf(
        "Pop", "Rock", "Electronic", "Hip-Hop", 
        "Jazz", "Classical", "Ambient", "Acoustic", 
        "Lo-Fi", "Indie", "Dance", "Chillout"
    )

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, selectedGenre = null, genreResults = emptyList()) }
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.update { it.copy(localResults = emptyList(), onlineResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300) // Debounce input
            _uiState.update { it.copy(isSearching = true) }

            // Search local
            launch {
                try {
                    val allLocal = localMusicRepository.getSongs().catch { emit(emptyList()) }.first()
                    val filtered = allLocal.filter {
                        it.title.contains(newQuery, ignoreCase = true) ||
                        it.artist.contains(newQuery, ignoreCase = true) ||
                        it.album.contains(newQuery, ignoreCase = true)
                    }
                    _uiState.update { it.copy(localResults = filtered) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(localResults = emptyList()) }
                }
            }

            // Search online Jamendo
            launch {
                val result = onlineMusicRepository.searchTracks(newQuery)
                result.onSuccess { tracks ->
                    _uiState.update { it.copy(onlineResults = tracks, isSearching = false) }
                }.onFailure {
                    _uiState.update { it.copy(onlineResults = emptyList(), isSearching = false) }
                }
            }
        }
    }

    fun selectGenre(genre: String) {
        if (_uiState.value.selectedGenre == genre) {
            _uiState.update { it.copy(selectedGenre = null, genreResults = emptyList()) }
            return
        }

        _uiState.update { 
            it.copy(
                selectedGenre = genre, 
                query = "", 
                localResults = emptyList(), 
                onlineResults = emptyList(), 
                isSearching = true
            ) 
        }

        viewModelScope.launch {
            val result = onlineMusicRepository.getTracksByGenre(genre.lowercase())
            result.onSuccess { tracks ->
                _uiState.update { it.copy(genreResults = tracks, isSearching = false) }
            }.onFailure {
                _uiState.update { it.copy(genreResults = emptyList(), isSearching = false) }
            }
        }
    }

    class Factory(
        private val localMusicRepository: LocalMusicRepository,
        private val onlineMusicRepository: OnlineMusicRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(localMusicRepository, onlineMusicRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
