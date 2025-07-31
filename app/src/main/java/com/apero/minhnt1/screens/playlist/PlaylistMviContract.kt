package com.apero.minhnt1.screens.playlist

import android.media.MediaMetadataRetriever
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class PlaylistMviState(
    var playlist: SnapshotStateList<Song> = mutableStateListOf(),
    var isList: MutableState<Boolean> = mutableStateOf(false),
    var retriever: MutableState<MediaMetadataRetriever> = mutableStateOf(MediaMetadataRetriever())
    ) {

}

sealed interface PlaylistMviIntents {
    data object SwitchView : PlaylistMviIntents
}

sealed interface PlaylistMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : PlaylistMviEvents
}