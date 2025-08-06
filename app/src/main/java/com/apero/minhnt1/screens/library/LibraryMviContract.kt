package com.apero.minhnt1.screens.library

import android.media.MediaMetadataRetriever
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.apero.minhnt1.database.song.Song

data class LibraryMviState(
    var songLibrary: SnapshotStateList<Song> = mutableStateListOf(),
    var remoteSongLibrary: SnapshotStateList<Song> = mutableStateListOf(),
    var remoteSongFetchState: MutableState<Int> = mutableStateOf(2),
    var isList: MutableState<Boolean> = mutableStateOf(false),
    var retriever: MutableState<MediaMetadataRetriever> = mutableStateOf(MediaMetadataRetriever())
)

sealed interface LibraryMviIntents {
    data object SwitchView : LibraryMviIntents
    data object GetMusicFromRemote : LibraryMviIntents
}

sealed interface LibraryMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : LibraryMviEvents
}