package com.flute.music.data.repository

import com.flute.music.data.db.dao.LikedSongDao
import com.flute.music.data.db.entity.LikedSongEntity
import com.flute.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LikedSongsRepository(private val likedSongDao: LikedSongDao) {

    fun getAllLikedSongs(): Flow<List<Song>> {
        return likedSongDao.getAllLikedSongs().map { entities ->
            entities.map { entity ->
                Song(
                    id = entity.songId,
                    title = entity.title,
                    artist = entity.artist,
                    album = entity.album,
                    albumId = -1L,
                    duration = entity.duration,
                    path = entity.path,
                    albumArtUri = entity.albumArtUri,
                    dateAdded = entity.likedAt / 1000L,
                    isLocal = entity.isLocal
                )
            }
        }
    }

    suspend fun likeSong(song: Song) {
        val entity = LikedSongEntity(
            songId = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            duration = song.duration,
            path = song.path,
            albumArtUri = song.albumArtUri,
            likedAt = System.currentTimeMillis(),
            isLocal = song.isLocal
        )
        likedSongDao.likeSong(entity)
    }

    suspend fun unlikeSong(songId: Long) {
        likedSongDao.unlikeSong(songId)
    }

    fun isLiked(songId: Long): Flow<Boolean> {
        return likedSongDao.isLiked(songId)
    }

    suspend fun toggleLike(song: Song, currentlyLiked: Boolean) {
        if (currentlyLiked) {
            unlikeSong(song.id)
        } else {
            likeSong(song)
        }
    }
}
