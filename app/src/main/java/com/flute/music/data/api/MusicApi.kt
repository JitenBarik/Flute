package com.flute.music.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

data class MusicApiResponse(
    @SerializedName("resultCount") val resultCount: Int = 0,
    @SerializedName("results") val results: List<MusicTrack> = emptyList()
)

data class MusicTrack(
    @SerializedName("trackId") val trackId: Long? = null,
    @SerializedName("trackName") val trackName: String? = null,
    @SerializedName("artistName") val artistName: String? = null,
    @SerializedName("collectionName") val collectionName: String? = null,
    @SerializedName("collectionId") val collectionId: Long? = null,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null,
    @SerializedName("artworkUrl100") val artworkUrl100: String? = null
)

interface MusicApi {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("media") media: String = "music",
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 30
    ): MusicApiResponse

    companion object {
        const val BASE_URL = "https://itunes.apple.com/"
    }
}
