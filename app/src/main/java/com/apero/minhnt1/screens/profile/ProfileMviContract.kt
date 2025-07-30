package com.apero.minhnt1.screens.profile

data class ProfileMviState(
    var name: String = "",
    var phoneNumber: String = "",
    var university: String = "",
    var selfDescription: String = "",

    )

sealed interface ProfileMviIntents {
    data object SaveData : ProfileMviIntents
}

sealed interface ProfileMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : ProfileMviEvents
}