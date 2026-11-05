package com.flute.music.data.model

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val createdAt: Long,
    val coverArtUri: String?
)
