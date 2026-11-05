package com.flute.music.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flute.music.data.model.Playlist
import com.flute.music.data.model.Song
import com.flute.music.data.repository.LikedSongsRepository
import com.flute.music.data.repository.LocalMusicRepository
import com.flute.music.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LibraryTab {
    OVERVIEW, ALL_SONGS, LIKED_SONGS, PLAYLISTS
}

data class LibraryUiState(
    val currentTab: LibraryTab = LibraryTab.OVERVIEW,
    val allSongs: List<Song> = emptyList(),
    val likedSongs: List<Song> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
    val showCreatePlaylistDialog: Boolean = false
)

class LibraryViewModel(
    private val localMusicRepository: LocalMusicRepository,
    private val playlistRepository: PlaylistRepository,
    private val likedSongsRepository: LikedSongsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            // Load local songs
            launch {
                localMusicRepository.getSongs()
                    .catch { emit(emptyList()) }
                    .collect { songs ->
                        _uiState.update { it.copy(allSongs = songs, isLoading = false) }
                    }
            }

            // Load liked songs
            launch {
                likedSongsRepository.getAllLikedSongs()
                    .catch { emit(emptyList()) }
                    .collect { liked ->
                        _uiState.update { it.copy(likedSongs = liked) }
                    }
            }

            // Load playlists
            launch {
                playlistRepository.getAllPlaylists()
                    .catch { emit(emptyList()) }
                    .collect { playlists ->
                        _uiState.update { it.copy(playlists = playlists) }
                    }
            }
        }
    }

    fun setTab(tab: LibraryTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun showCreatePlaylistDialog(show: Boolean) {
        _uiState.update { it.copy(showCreatePlaylistDialog = show) }
    }

    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            playlistRepository.createPlaylist(name.trim())
            showCreatePlaylistDialog(false)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    class Factory(
        private val localMusicRepository: LocalMusicRepository,
        private val playlistRepository: PlaylistRepository,
        private val likedSongsRepository: LikedSongsRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
                return LibraryViewModel(
                    localMusicRepository,
                    playlistRepository,
                    likedSongsRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
