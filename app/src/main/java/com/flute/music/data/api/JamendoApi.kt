package com.flute.music.data.api

import com.flute.music.data.model.JamendoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JamendoApi {

    @GET("tracks/")
    suspend fun searchTracks(
        @Query("client_id") clientId: String = CLIENT_ID,
        @Query("format") format: String = "jsonpretty",
        @Query("limit") limit: Int = 30,
        @Query("search") search: String,
        @Query("order") order: String = "popularity_total",
        @Query("include") include: String = "musicinfo"
    ): JamendoResponse

    @GET("tracks/")
    suspend fun getTracksByTags(
        @Query("client_id") clientId: String = CLIENT_ID,
        @Query("format") format: String = "jsonpretty",
        @Query("limit") limit: Int = 30,
        @Query("fuzzytags") fuzzytags: String,
        @Query("order") order: String = "popularity_total",
        @Query("include") include: String = "musicinfo"
    ): JamendoResponse

    @GET("tracks/")
    suspend fun getTrendingTracks(
        @Query("client_id") clientId: String = CLIENT_ID,
        @Query("format") format: String = "jsonpretty",
        @Query("limit") limit: Int = 20,
        @Query("order") order: String = "popularity_week",
        @Query("include") include: String = "musicinfo"
    ): JamendoResponse

    companion object {
        const val BASE_URL = "https://api.jamendo.com/v3.0/"
        const val CLIENT_ID = "b0845b38"
    }
}
