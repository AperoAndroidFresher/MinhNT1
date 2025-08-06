package com.apero.minhnt1.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter
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
        return value?.let { "${it.cover},${it.title},${it.artist},${it.duration},${it.path},${it.songID},${it.inPlaylistID}" }
    }

    @TypeConverter
    fun toSong(value: String?): Song? {
        return value?.split(",")?.let { parts ->
            if (parts.size == 7) {
                Song(
                    parts[0].substringAfterLast('=').toUri(),
                    parts[1].substringAfterLast('=').trim(),
                    parts[2].substringAfterLast('=').trim(),
                    parts[3].substringAfterLast('=').trim().toLong(),
                    parts[4].substringAfterLast('=').trim(),
                    parts[5].substringAfterLast('=').trim().toInt(),
                    parts[6].substringAfterLast('=').substringBeforeLast(')').toInt()
                )
            } else null
        }
    }

    @TypeConverter
    fun fromSongList(value: MutableList<Song?>): String? {
        var songList = ""
        if (value.isNotEmpty()) {
            for (i in 0..value.size - 1) {
                songList += if (i == 0) value[i]
                else "&" + fromSong(value[i])
            }
        }
        return songList
    }

    @TypeConverter
    fun toSongList(value: String?): MutableList<Song?> {
        var list = mutableListOf<Song?>()
        var decodedString = value?.split('&')
        if (decodedString != null) {
            for (s in decodedString) {
                if (s.isNotEmpty()) {
                    list.add(toSong(s))
                }
            }
        }
        return list
    }

}