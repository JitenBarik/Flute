package com.flute.music.data.model

import com.google.gson.annotations.SerializedName

data class JamendoResponse(
    @SerializedName("headers") val headers: JamendoHeaders,
    @SerializedName("results") val results: List<JamendoTrack>
)

data class JamendoHeaders(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: Int,
    @SerializedName("error_message") val error_message: String,
    @SerializedName("warnings") val warnings: String,
    @SerializedName("results_count") val results_count: Int
)

data class JamendoTrack(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("artist_id") val artist_id: String,
    @SerializedName("artist_name") val artist_name: String,
    @SerializedName("album_name") val album_name: String,
    @SerializedName("album_id") val album_id: String,
    @SerializedName("album_image") val album_image: String,
    @SerializedName("audio") val audio: String,
    @SerializedName("audiodownload") val audiodownload: String,
    @SerializedName("image") val image: String
)

fun JamendoTrack.toSong(): Song {
    return Song(
        id = id.toLongOrNull() ?: id.hashCode().toLong(),
        title = name,
        artist = artist_name,
        album = album_name,
        albumId = album_id.toLongOrNull() ?: album_id.hashCode().toLong(),
        duration = duration.toLong() * 1000L,
        path = audio,
        albumArtUri = album_image,
        dateAdded = System.currentTimeMillis() / 1000L,
        isLocal = false
    )
}
