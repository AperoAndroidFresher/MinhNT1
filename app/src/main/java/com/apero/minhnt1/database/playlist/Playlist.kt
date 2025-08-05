package com.apero.minhnt1.database.playlist

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.apero.minhnt1.R
import com.apero.minhnt1.database.Converters
import com.apero.minhnt1.database.song.Song


@Entity
@TypeConverters(Converters::class)
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistID: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "playlist_cover") var playlistCover: Int = R.drawable.cover_1,
    @ColumnInfo(name = "songList") var songList: SnapshotStateList<Song?> = mutableStateListOf(),
    @ColumnInfo(name = "creator_id") var creatorID: Int = 0
)

//@Entity(primaryKeys = ["playlistID", "songID"])
//data class PlaylistSongCrossRef(
//    val playlistID: Int,
//    val songID: Int
//)
//
//data class PlaylistWithSongs(
//    @Embedded val playlist: Playlist,
//    @Relation(
//        parentColumn = "playlistID",
//        entityColumn = "songID",
//        associateBy = Junction(PlaylistSongCrossRef::class)
//    )
//    val songs: List<Song>
//)
