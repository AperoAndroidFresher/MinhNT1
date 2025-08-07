package com.apero.minhnt1.database.song

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.apero.minhnt1.database.Converters

@Entity
@TypeConverters(Converters::class)
data class Song(
    @ColumnInfo(name = "cover") val cover: Uri = "".toUri(),
    @ColumnInfo(name = "title") val title: String = "Sample",
    @ColumnInfo(name = "artist") val artist: String = "Sample",
    @ColumnInfo(name = "duration") val duration: Long = 0,
    @ColumnInfo(name = "path", typeAffinity = 2) var path: String = "",
    @PrimaryKey(autoGenerate = true) val songID: Int = 0,
    @ColumnInfo(name = "inPlaylistID") var inPlaylistID: Int = 0,
    // 0 = false, 1 = true. Using Int to better fit into database
    @ColumnInfo(name = "isLocal") var isLocal: Int
)

