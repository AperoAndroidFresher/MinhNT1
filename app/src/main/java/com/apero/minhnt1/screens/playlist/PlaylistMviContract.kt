package com.apero.minhnt1.screens.playlist

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.apero.minhnt1.Song

data class PlaylistMviState(
    var playlist: SnapshotStateList<Song> = mutableStateListOf(),
    var isList: MutableState<Boolean> = mutableStateOf(true),

    ) {

}

sealed interface PlaylistMviIntents {
    data object SwitchView : PlaylistMviIntents
}

sealed interface PlaylistMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : PlaylistMviEvents
}