package com.flute.music.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.flute.music.data.db.dao.LikedSongDao
import com.flute.music.data.db.dao.PlaylistDao
import com.flute.music.data.db.dao.RecentSongDao
import com.flute.music.data.db.entity.LikedSongEntity
import com.flute.music.data.db.entity.PlaylistEntity
import com.flute.music.data.db.entity.PlaylistSongCrossRef
import com.flute.music.data.db.entity.RecentSongEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        LikedSongEntity::class,
        RecentSongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FluteDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao
    abstract fun likedSongDao(): LikedSongDao
    abstract fun recentSongDao(): RecentSongDao

    companion object {
        @Volatile
        private var INSTANCE: FluteDatabase? = null

        fun getInstance(context: Context): FluteDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FluteDatabase::class.java,
                    "flute_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
