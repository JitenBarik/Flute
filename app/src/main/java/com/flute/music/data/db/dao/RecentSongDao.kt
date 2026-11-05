package com.flute.music.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flute.music.data.db.entity.RecentSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSongDao {

    @Query("SELECT * FROM recent_songs ORDER BY playedAt DESC LIMIT 50")
    fun getRecentSongs(): Flow<List<RecentSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecentSong(recentSong: RecentSongEntity)

    @Query("DELETE FROM recent_songs")
    suspend fun clearHistory()
}
