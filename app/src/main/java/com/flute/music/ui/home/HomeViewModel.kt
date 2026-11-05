package com.flute.music.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flute.music.data.model.Song
import com.flute.music.data.repository.LocalMusicRepository
import com.flute.music.data.repository.OnlineMusicRepository
import com.flute.music.data.repository.RecentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val greeting: String = "Hello",
    val recentSongs: List<Song> = emptyList(),
    val quickPicks: List<Song> = emptyList(),
    val trendingTracks: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class HomeViewModel(
    private val localMusicRepository: LocalMusicRepository,
    private val onlineMusicRepository: OnlineMusicRepository,
    private val recentRepository: RecentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateGreeting()
        loadHomeData()
    }

    private fun updateGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 4..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        _uiState.update { it.copy(greeting = greeting) }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // 1. Load Recent Songs
            launch {
                recentRepository.getRecentSongs()
                    .catch { emit(emptyList()) }
                    .collect { recents ->
                        _uiState.update { it.copy(recentSongs = recents) }
                    }
            }

            // 2. Load Local Quick Picks
            launch {
                try {
                    val localSongs = localMusicRepository.getSongs()
                        .catch { emit(emptyList()) }
                        .first()
                    val quickPicks = localSongs.shuffled().take(10)
                    _uiState.update { it.copy(quickPicks = quickPicks) }
                } catch (e: Exception) {
                    // Ignore local failure if permissions not yet granted
                }
            }

            // 3. Load Online Trending Tracks
            launch {
                val trendingResult = onlineMusicRepository.getTrendingTracks()
                trendingResult.onSuccess { tracks ->
                    _uiState.update { it.copy(trendingTracks = tracks, isLoading = false) }
                }.onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
                }
            }
        }
    }

    class Factory(
        private val localMusicRepository: LocalMusicRepository,
        private val onlineMusicRepository: OnlineMusicRepository,
        private val recentRepository: RecentRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(
                    localMusicRepository,
                    onlineMusicRepository,
                    recentRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
