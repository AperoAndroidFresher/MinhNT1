package com.apero.minhnt1.screens.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.apero.minhnt1.User
import com.apero.minhnt1.utility.validateInput

data class LoginMviState(
    var users: MutableList<User> = mutableListOf(),
    var username: MutableState<String> = mutableStateOf(""),
    var password: MutableState<String> = mutableStateOf(""),
    var confirmPassword: MutableState<String> = mutableStateOf(""),
    var email: MutableState<String> = mutableStateOf(""),
    var selfDescription: MutableState<String> = mutableStateOf(""),
    var loginSuccess: MutableState<Boolean> = mutableStateOf(false),

    ) {
    var usernameFormatCheck = mutableStateOf(true)
    var passwordFormatCheck = mutableStateOf(true)
    var confirmPasswordCheck = mutableStateOf(true)
    var emailFormatCheck = mutableStateOf(true)
}

abstract class LoginMviIntents(open val username: String, open val password: String) {
    data class LogIn(override val username: String, override val password: String) :
        LoginMviIntents(username, password)

    data class SignUp(
        override val username: String,
        override val password: String,
        val confirmPassword: String,
        val email: String
    ) : LoginMviIntents(username, password)
}

sealed interface LoginMviEvents {
    data class ShowDialog(val icon: Int, val message: String) : LoginMviEvents
}