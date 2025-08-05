package com.apero.minhnt1.database

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.room.TypeConverter
import com.apero.minhnt1.database.playlist.Playlist
import com.apero.minhnt1.database.song.Song

class Converters {

    @TypeConverter
    fun fromUri(value: Uri?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toUri(value: String?): Uri? {
        return value?.toUri()
    }

    @TypeConverter
    fun fromSong(value: Song?): String? {
        return value?.let {"${it.cover},${it.title},${it.artist},${it.duration},${it.path},${it.songID}"}
    }

    @TypeConverter
    fun toSong(value: String?): Song? {
        return value?.split(",")?.let {parts ->
            if (parts.size == 6) {
                Song(parts[0].toUri(), parts[1], parts[2], parts[3].toLong(), parts[4], parts[5].toInt())
            } else null
        }
    }

//    @TypeConverter
//    fun fromPlaylist(value: Playlist?): String? {
//        var songList = ""
//        for (i in 0..value?.songList!!.size) {
//            songList += if (i == 0) value.songList[i]
//            else "&" + fromSong(value.songList[i])
//        }
//
//        return value.let {"${it.playlistID},${it.name},${it.playlistCover}," + songList}
//    }

    @TypeConverter
    fun fromSongList(value: SnapshotStateList<Song?>): String? {
        var songList = ""
        for (i in 0..value.size) {
            songList += if (i == 0) value[i]
            else "&" + fromSong(value[i])
        }

        return songList
    }

    @TypeConverter
    fun toSongList(value: String?): SnapshotStateList<Song?> {
        var list = mutableStateListOf<Song?>()
        var decodedString = value?.split('&')
        if (decodedString != null) {
            for (s in decodedString) {
                list.add(toSong(s))
            }
        }
        return list
    }

    private fun recreatePlaylist(value: String): SnapshotStateList<Song> {
        var splitString = value.split("&")
        var playlist = mutableStateListOf<Song>()
        for (song in splitString) {
            playlist.add(toSong(song)!!)
        }
        return playlist
    }

//    @TypeConverter
//    fun toPlaylist(value: String?): Playlist? {
//        return value?.split(",")?.let {parts ->
//            if (parts.size == 4) {
//                Playlist(parts[0].toInt(), parts[1], parts[2].toInt(), recreatePlaylist(parts[3]))
//            } else null
//        }
//    }
}