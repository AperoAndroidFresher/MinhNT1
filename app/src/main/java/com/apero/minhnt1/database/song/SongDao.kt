package com.apero.minhnt1.database.song

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    suspend fun getAll(): List<Song>

    @Insert
    suspend fun insert(song: Song)

    @Delete
    suspend fun delete(song: Song)
}