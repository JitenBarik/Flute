package com.flute.music.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flute.music.data.db.entity.LikedSongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LikedSongDao {

    @Query("SELECT * FROM liked_songs ORDER BY likedAt DESC")
    fun getAllLikedSongs(): Flow<List<LikedSongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun likeSong(likedSong: LikedSongEntity)

    @Query("DELETE FROM liked_songs WHERE songId = :songId")
    suspend fun unlikeSong(songId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM liked_songs WHERE songId = :songId)")
    fun isLiked(songId: Long): Flow<Boolean>
}
