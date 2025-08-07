package com.apero.minhnt1.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apero.minhnt1.Username
import com.apero.minhnt1.database.user.User
import com.apero.minhnt1.utility.validateInput
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<LoginMviState>(LoginMviState())
    val state: StateFlow<LoginMviState> = _state.asStateFlow()
    private val _event = MutableSharedFlow<LoginMviEvents>()
    val event: SharedFlow<LoginMviEvents> = _event.asSharedFlow()
    private val _intentFlow = MutableSharedFlow<LoginMviIntents>()

    fun processIntent(intent: LoginMviIntents) {
        //   viewModelScope.launch {
        //       _intentFlow.collect { intent ->
        when (intent) {
            is LoginMviIntents.LogIn -> login(
                intent.username,
                intent.password,
                intent.isRememberMeChecked
            )

            is LoginMviIntents.SignUp -> signup(
                intent.username,
                intent.password,
                intent.confirmPassword,
                intent.email
            )
        }
        //      }
        //  }
    }

    private fun login(username: String, password: String, isRememberMeChecked: Boolean) {
        val usernameFormatCheck = validateInput(username, "USERNAME")
        val passwordFormatCheck = validateInput(password, "PASSWORD")
        if (usernameFormatCheck && passwordFormatCheck) {
            val foundUser =
                _state.value.users.filter { it.username == username && it.password == password }
            if (foundUser.isNotEmpty()) {
                _state.value.users.add(foundUser[0])
                _state.value.loginSuccess.value = true
                _state.value.isRememberMeChecked.value = isRememberMeChecked
                Username.value = foundUser[0].username
            }
        }
    }

    private fun signup(username: String, password: String, confirmPassword: String, email: String) {
        val usernameFormatCheck = validateInput(username, "USERNAME")
        val passwordFormatCheck = validateInput(password, "PASSWORD")
        val confirmPasswordCheck =
            validateInput(confirmPassword, "PASSWORD") && (password == confirmPassword)
        val emailFormatCheck = validateInput(email, "EMAIL")
        if (usernameFormatCheck && passwordFormatCheck && confirmPasswordCheck && emailFormatCheck) {
            _state.value.users.add(User(username = username, password = password, email = email))
            _state.value.username.value = ""
            _state.value.password.value = ""
            _state.value.emailAddress.value = ""
            _state.value.isSignupScreen.value = false

        } else {
            if (!usernameFormatCheck) {
                _state.value.username.value = ""
            }
            if (!passwordFormatCheck || confirmPasswordCheck) {
                _state.value.password.value = ""
                _state.value.confirmPassword.value = ""
            }
            if (emailFormatCheck) {
                _state.value.emailAddress.value = ""
            }
        }
    }

    private fun sendEvent(event: LoginMviEvents) {
        viewModelScope.launch {
            _event.emit(event)
        }
    }


}