package com.apero.minhnt1.screens.library

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class LibraryViewModel : ViewModel() {
    private val _state = MutableStateFlow<LibraryMviState>(LibraryMviState())
    val state: StateFlow<LibraryMviState> = _state.asStateFlow()
    private val _event = MutableSharedFlow<LibraryMviEvents>()
    val event: SharedFlow<LibraryMviEvents> = _event.asSharedFlow()
    private val _intentFlow = MutableSharedFlow<LibraryMviIntents>()

    fun processIntent(intent: LibraryMviIntents) {
        //   viewModelScope.launch {
        //       _intentFlow.collect { intent ->
        when (intent) {
            is LibraryMviIntents.SwitchView -> switchView()
        }
        //      }
        //  }
    }

    private fun switchView() {
        _state.value.isList.value = !_state.value.isList.value
    }
}