package com.apero.minhnt1.screens.playlist

import android.media.MediaMetadataRetriever
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.apero.minhnt1.R
import com.apero.minhnt1.screens.library.Song

data class PlaylistMviState(
    var playlistLibrary: SnapshotStateList<Playlist> = mutableStateListOf(),
    var isList: MutableState<Boolean> = mutableStateOf(false),
    var showRenameDialog: MutableState<Boolean> = mutableStateOf(false),

    var retriever: MutableState<MediaMetadataRetriever> = mutableStateOf(MediaMetadataRetriever())
)

data class Playlist(var name: String, var playlistCover: Int = R.drawable.cover_1, var playlist: SnapshotStateList<Song> = mutableStateListOf())

sealed interface PlaylistMviIntents {
    data object SwitchView : PlaylistMviIntents
}

sealed interface PlaylistMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : PlaylistMviEvents
}