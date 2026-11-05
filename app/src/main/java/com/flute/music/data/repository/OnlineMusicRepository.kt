package com.flute.music.data.repository

import com.flute.music.data.api.MusicApi
import com.flute.music.data.api.MusicTrack
import com.flute.music.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineMusicRepository(private val musicApi: MusicApi) {

    suspend fun searchTracks(query: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = musicApi.search(term = query)
            val songs = response.results
                .filter { !it.previewUrl.isNullOrEmpty() }
                .map { it.toSong() }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingTracks(): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            // Default top hits search
            val response = musicApi.search(term = "hits", limit = 25)
            val songs = response.results
                .filter { !it.previewUrl.isNullOrEmpty() }
                .map { it.toSong() }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTracksByGenre(genre: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = musicApi.search(term = genre, limit = 25)
            val songs = response.results
                .filter { !it.previewUrl.isNullOrEmpty() }
                .map { it.toSong() }
            Result.success(songs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun MusicTrack.toSong(): Song {
        val highResArt = artworkUrl100?.replace("100x100bb", "600x600bb") ?: artworkUrl100
        return Song(
            id = trackId ?: hashCode().toLong(),
            title = trackName ?: "Unknown Track",
            artist = artistName ?: "Unknown Artist",
            album = collectionName ?: "Single",
            albumId = collectionId ?: -1L,
            duration = trackTimeMillis ?: 30000L,
            path = previewUrl ?: "",
            albumArtUri = highResArt,
            dateAdded = System.currentTimeMillis() / 1000L,
            isLocal = false
        )
    }
}
