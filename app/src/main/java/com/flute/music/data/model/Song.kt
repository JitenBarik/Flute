package com.flute.music.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val path: String,
    val albumArtUri: String?,
    val dateAdded: Long,
    val isLocal: Boolean = true
) {
    val durationFormatted: String
        get() {
            val totalSeconds = duration / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val contentUri: Uri
        get() = Uri.parse(path)

    companion object {
        val EMPTY = Song(
            id = -1L,
            title = "",
            artist = "",
            album = "",
            albumId = -1L,
            duration = 0L,
            path = "",
            albumArtUri = null,
            dateAdded = 0L,
            isLocal = true
        )
    }
}
