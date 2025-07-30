package com.apero.minhnt1.screens.playlist

import androidx.compose.runtime.mutableStateOf
import com.apero.minhnt1.Song

data class PlaylistMviState(
    var playlist: MutableList<Song> = mutableListOf(),
    var isGrid: Boolean = false

    ) {

}

sealed interface ProfileMviIntents {
    data object SaveData : ProfileMviIntents
}

sealed interface ProfileMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : ProfileMviEvents
}