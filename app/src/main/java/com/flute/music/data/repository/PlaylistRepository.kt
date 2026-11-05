package com.flute.music.data.repository

import com.flute.music.data.db.dao.PlaylistDao
import com.flute.music.data.db.entity.PlaylistEntity
import com.flute.music.data.db.entity.PlaylistSongCrossRef
import com.flute.music.data.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    songCount = 0,
                    createdAt = entity.createdAt,
                    coverArtUri = null
                )
            }
        }
    }

    suspend fun getPlaylistById(playlistId: Long): Playlist? {
        return playlistDao.getPlaylistById(playlistId)?.let { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                songCount = 0,
                createdAt = entity.createdAt,
                coverArtUri = null
            )
        }
    }

    suspend fun createPlaylist(name: String): Long {
        val entity = PlaylistEntity(
            name = name,
            createdAt = System.currentTimeMillis()
        )
        return playlistDao.createPlaylist(entity)
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val crossRef = PlaylistSongCrossRef(
            playlistId = playlistId,
            songId = songId,
            addedAt = System.currentTimeMillis()
        )
        playlistDao.addSongToPlaylist(crossRef)
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>> {
        return playlistDao.getSongIdsForPlaylist(playlistId)
    }
}
