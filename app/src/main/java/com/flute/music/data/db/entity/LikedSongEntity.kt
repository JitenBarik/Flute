package com.flute.music.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_songs")
data class LikedSongEntity(
    @PrimaryKey
    val songId: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val path: String,
    val albumArtUri: String?,
    val likedAt: Long = System.currentTimeMillis(),
    val isLocal: Boolean
)
