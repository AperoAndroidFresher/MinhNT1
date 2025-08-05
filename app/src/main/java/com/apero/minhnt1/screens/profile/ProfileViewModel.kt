package com.apero.minhnt1.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apero.minhnt1.screens.playlist.PlaylistMviIntents
import com.apero.minhnt1.utility.validateInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow<ProfileMviState>(ProfileMviState())
    val state: StateFlow<ProfileMviState> = _state.asStateFlow()
    private val _event = MutableSharedFlow<ProfileMviEvents>()
    val event: SharedFlow<ProfileMviEvents> = _event.asSharedFlow()
    private val _intentFlow = MutableSharedFlow<PlaylistMviIntents>()

    fun processIntent(intent: ProfileMviIntents) {
        //   viewModelScope.launch {
        //       _intentFlow.collect { intent ->
        when (intent) {
            is ProfileMviIntents.Submit -> submit(
                intent.name,
                intent.phoneNumber,
                intent.university,
                intent.selfDescription
            )

            is ProfileMviIntents.ToggleDarkMode -> toggleDarkMode()
        }
        //      }
        //  }
    }

    private fun toggleDarkMode() {
        _state.value.isDarkMode.value = !_state.value.isDarkMode.value
    }

    private fun submit(
        name: String,
        phoneNumber: String,
        university: String,
        selfDescription: String
    ) {
        _state.value.isNameFormatValid.value = validateInput(name, "NAME")
        _state.value.isPhoneNumberFormatValid.value = validateInput(phoneNumber, "PHONE NUMBER")
        _state.value.isUniversityNameFormatValid.value = validateInput(university, "UNIVERSITY NAME")

        if (_state.value.isNameFormatValid.value && _state.value.isPhoneNumberFormatValid.value && _state.value.isUniversityNameFormatValid.value) {
            _state.value.name.value = name
            _state.value.phoneNumber.value = phoneNumber
            _state.value.universityName.value = university
            _state.value.selfDescription.value = selfDescription
            _state.value.isEditable.value = false
            _state.value.isEditButtonClickable.value = true
            _state.value.shouldRevealSubmit.value = false
        }
    }

    private fun sendEvent(event: ProfileMviEvents) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }
}