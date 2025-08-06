package com.apero.minhnt1.database.playlist

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
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
    @ColumnInfo(name = "songList") var songList: MutableList<Song?> = mutableListOf(),
    @ColumnInfo(name = "creator_id") var creatorID: Int = 0
)

@Entity(primaryKeys = ["playlistID", "inPlaylistID"])
data class PlaylistSongCrossRef(
    val playlistID: Int,
    val inPlaylistID: Int
)

data class PlaylistWithSongs(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "playlistID",
        entityColumn = "inPlaylistID",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<Song>
)
