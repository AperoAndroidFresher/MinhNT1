package com.apero.minhnt1.screens.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.apero.minhnt1.database.user.User

data class LoginMviState(
    var users: MutableList<User> = mutableListOf(),
    var username: MutableState<String> = mutableStateOf(""),
    var password: MutableState<String> = mutableStateOf(""),
    var confirmPassword: MutableState<String> = mutableStateOf(""),
    var emailAddress: MutableState<String> = mutableStateOf(""),
    var selfDescription: MutableState<String> = mutableStateOf(""),
    var loginSuccess: MutableState<Boolean> = mutableStateOf(false),
    var usernameFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var passwordFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var confirmPasswordCheck: MutableState<Boolean> = mutableStateOf(true),
    var emailFormatCheck: MutableState<Boolean> = mutableStateOf(true),
    var isSignupScreen: MutableState<Boolean> = mutableStateOf(false),
    var isPasswordVisible: MutableState<Boolean> = mutableStateOf(false)
)

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