package com.apero.minhnt1.database.song

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    suspend fun getAll(): MutableList<Song>

    @Insert(onConflict = IGNORE)
    suspend fun insert(song: Song)

    @Delete
    suspend fun delete(song: Song)

    @Query("SELECT * FROM song WHERE title = :title")
    suspend fun getSong(title: String): List<Song>

    @Query("update song set path = :path where title = :title")
    suspend fun updateSongPath(title: String, path: String)

    @Query("SELECT * FROM song WHERE isLocal = 0")
    suspend fun getAllRemoteSongs(): MutableList<Song>
}