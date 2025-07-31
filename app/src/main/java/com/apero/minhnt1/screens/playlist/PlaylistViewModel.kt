package com.apero.minhnt1.screens.playlist

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaylistViewModel : ViewModel() {
    private val _state = MutableStateFlow<PlaylistMviState>(PlaylistMviState())
    val state: StateFlow<PlaylistMviState> = _state.asStateFlow()
    private val _event = MutableSharedFlow<PlaylistMviEvents>()
    val event: SharedFlow<PlaylistMviEvents> = _event.asSharedFlow()
    private val _intentFlow = MutableSharedFlow<PlaylistMviIntents>()

    fun processIntent(intent: PlaylistMviIntents) {
        //   viewModelScope.launch {
        //       _intentFlow.collect { intent ->
        when (intent) {
            is PlaylistMviIntents.SwitchView -> switchView()
        }
        //      }
        //  }
    }

    private fun switchView() {
        _state.value.isList.value = !_state.value.isList.value
    }

    private fun populatePlaylist() {

    }
}