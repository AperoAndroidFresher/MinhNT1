package com.apero.minhnt1.database.song

import androidx.compose.runtime.snapshots.SnapshotStateList
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
}