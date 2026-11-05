package com.flute.music.data.repository

import com.flute.music.data.api.JamendoApi
import com.flute.music.data.model.Song
import com.flute.music.data.model.toSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OnlineMusicRepository(private val jamendoApi: JamendoApi) {

    suspend fun searchTracks(query: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = jamendoApi.searchTracks(search = query)
            if (response.headers.code == 0) {
                Result.success(response.results.map { it.toSong() })
            } else {
                Result.failure(Exception("Jamendo API error: ${response.headers.error_message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingTracks(): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = jamendoApi.getTrendingTracks()
            if (response.headers.code == 0) {
                Result.success(response.results.map { it.toSong() })
            } else {
                Result.failure(Exception("Jamendo API error: ${response.headers.error_message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTracksByGenre(genre: String): Result<List<Song>> = withContext(Dispatchers.IO) {
        try {
            val response = jamendoApi.getTracksByTags(fuzzytags = genre)
            if (response.headers.code == 0) {
                Result.success(response.results.map { it.toSong() })
            } else {
                Result.failure(Exception("Jamendo API error: ${response.headers.error_message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
