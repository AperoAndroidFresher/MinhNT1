package com.apero.minhnt1.screens.profile

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ProfileMviState(

    var name: MutableState<String> = mutableStateOf(""),
    var phoneNumber: MutableState<String> = mutableStateOf(""),
    var universityName: MutableState<String> = mutableStateOf(""),
    var selfDescription: MutableState<String> = mutableStateOf(""),
    var isDarkMode: MutableState<Boolean> = mutableStateOf(true),
    var isEditable: MutableState<Boolean> = mutableStateOf(false),
    var isEditButtonClickable: MutableState<Boolean> = mutableStateOf(true),
    var shouldRevealSubmit: MutableState<Boolean> = mutableStateOf(false),
    var isNameFormatValid: MutableState<Boolean> = mutableStateOf(true),
    var isPhoneNumberFormatValid: MutableState<Boolean> = mutableStateOf(true),
    var isUniversityNameFormatValid: MutableState<Boolean> = mutableStateOf(true),
    var shouldShowAlert: MutableState<Boolean> = mutableStateOf(false)
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