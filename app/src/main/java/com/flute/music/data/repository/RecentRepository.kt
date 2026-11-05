package com.flute.music.data.repository

import com.flute.music.data.db.dao.RecentSongDao
import com.flute.music.data.db.entity.RecentSongEntity
import com.flute.music.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecentRepository(private val recentSongDao: RecentSongDao) {

    fun getRecentSongs(): Flow<List<Song>> {
        return recentSongDao.getRecentSongs().map { entities ->
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
                    dateAdded = entity.playedAt / 1000L,
                    isLocal = entity.isLocal
                )
            }
        }
    }

    suspend fun addRecentSong(song: Song) {
        val entity = RecentSongEntity(
            songId = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            duration = song.duration,
            path = song.path,
            albumArtUri = song.albumArtUri,
            playedAt = System.currentTimeMillis(),
            isLocal = song.isLocal
        )
        recentSongDao.addRecentSong(entity)
    }

    suspend fun clearHistory() {
        recentSongDao.clearHistory()
    }
}
