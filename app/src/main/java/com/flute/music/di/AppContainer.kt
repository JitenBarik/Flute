package com.flute.music.di

import android.app.Application
import com.flute.music.data.api.JamendoApi
import com.flute.music.data.db.FluteDatabase
import com.flute.music.data.repository.LikedSongsRepository
import com.flute.music.data.repository.LocalMusicRepository
import com.flute.music.data.repository.OnlineMusicRepository
import com.flute.music.data.repository.PlaylistRepository
import com.flute.music.data.repository.RecentRepository
import com.flute.music.player.MusicPlayerManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainer(private val application: Application) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(JamendoApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val database: FluteDatabase by lazy {
        FluteDatabase.getInstance(application)
    }

    val jamendoApi: JamendoApi by lazy {
        retrofit.create(JamendoApi::class.java)
    }

    val localMusicRepository: LocalMusicRepository by lazy {
        LocalMusicRepository(application)
    }

    val onlineMusicRepository: OnlineMusicRepository by lazy {
        OnlineMusicRepository(jamendoApi)
    }

    val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(database.playlistDao())
    }

    val likedSongsRepository: LikedSongsRepository by lazy {
        LikedSongsRepository(database.likedSongDao())
    }

    val recentRepository: RecentRepository by lazy {
        RecentRepository(database.recentSongDao())
    }

    val musicPlayerManager: MusicPlayerManager by lazy {
        MusicPlayerManager(application)
    }
}
