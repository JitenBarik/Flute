package com.flute.music.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.flute.music.data.model.Song
import com.flute.music.player.MusicPlayerManager
import com.flute.music.player.PlayerState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlayerViewModel(
    private val musicPlayerManager: MusicPlayerManager
) : ViewModel() {

    val playerState: StateFlow<PlayerState> = musicPlayerManager.playerState

    val currentSong: StateFlow<Song> = playerState
        .map { it.currentSong }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Song.EMPTY)

    val isPlaying: StateFlow<Boolean> = playerState
        .map { it.isPlaying }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val progress: StateFlow<Float> = playerState
        .map { state ->
            if (state.duration > 0L) {
                (state.currentPosition.toFloat() / state.duration.toFloat()).coerceIn(0f, 1f)
            } else {
                0f
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val currentPositionFormatted: StateFlow<String> = playerState
        .map { formatDuration(it.currentPosition) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0:00")

    val durationFormatted: StateFlow<String> = playerState
        .map { formatDuration(it.duration) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0:00")

    fun playSong(song: Song) {
        musicPlayerManager.playSong(song)
    }

    fun playAll(songs: List<Song>, startIndex: Int) {
        musicPlayerManager.playAll(songs, startIndex)
    }

    fun togglePlayPause() {
        if (musicPlayerManager.isPlaying) {
            musicPlayerManager.pause()
        } else {
            musicPlayerManager.resume()
        }
    }

    fun next() {
        musicPlayerManager.next()
    }

    fun previous() {
        musicPlayerManager.previous()
    }

    fun seekTo(fraction: Float) {
        val state = playerState.value
        if (state.duration > 0L) {
            val position = (fraction * state.duration).toLong().coerceIn(0L, state.duration)
            musicPlayerManager.seekTo(position)
        }
    }

    fun toggleShuffle() {
        musicPlayerManager.toggleShuffle()
    }

    fun cycleRepeatMode() {
        musicPlayerManager.cycleRepeatMode()
    }

    fun addToQueue(song: Song) {
        musicPlayerManager.addToQueue(song)
    }

    companion object {
        fun formatDuration(millis: Long): String {
            if (millis <= 0L) return "0:00"
            val totalSeconds = millis / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "$minutes:${seconds.toString().padStart(2, '0')}"
        }
    }

    class Factory(
        private val musicPlayerManager: MusicPlayerManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
                return PlayerViewModel(musicPlayerManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
