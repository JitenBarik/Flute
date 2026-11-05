package com.flute.music.player

import com.flute.music.data.model.Song

data class PlayerState(
    val currentSong: Song = Song.EMPTY,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1
)

enum class RepeatMode {
    OFF, ONE, ALL
}
