package com.apero.minhnt1.screens.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

data class ProfileMviState(
    var name: MutableState<String> = mutableStateOf(""),
    var phoneNumber: MutableState<String> = mutableStateOf(""),
    var university: MutableState<String> = mutableStateOf(""),
    var selfDescription: MutableState<String> = mutableStateOf(""),
    var isDarkMode: MutableState<Boolean> = mutableStateOf(true),
    var editable: MutableState<Boolean> = mutableStateOf(false),
    var editButtonClickable: MutableState<Boolean> = mutableStateOf(true),
    var revealSubmit: MutableState<Boolean> = mutableStateOf(false),
    var nameFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var phoneNumberFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var universityFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var showAlert: MutableState<Boolean> = mutableStateOf(false)
)

sealed interface ProfileMviIntents {
    data class Submit(
        val name: String,
        val phoneNumber: String,
        val university: String,
        val selfDescription: String
    ) : ProfileMviIntents

    data object ToggleDarkMode : ProfileMviIntents
}

sealed interface ProfileMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : ProfileMviEvents
}