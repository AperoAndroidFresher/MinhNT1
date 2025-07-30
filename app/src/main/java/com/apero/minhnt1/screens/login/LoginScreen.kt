package com.apero.minhnt1.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.minhnt1.ErrorMessage
import com.apero.minhnt1.Home
import com.apero.minhnt1.R
import com.apero.minhnt1.RememberCheckbox
import com.apero.minhnt1.Screen
import com.apero.minhnt1.UserEntry
import com.apero.minhnt1.utility.validateInput

@Composable
//@Preview(showBackground = true)
fun LoginScreen(
    viewModel: LoginViewModel,
    backStack: SnapshotStateList<Screen>,
    success: MutableState<Boolean>
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    //var userEntries = remember { mutableStateListOf<UserEntry>() }
    //var successfulLogin by remember { mutableStateOf(false) }


//    var usernameCheck by remember { mutableStateOf(false) }
//
//    var password by remember { mutableStateOf("") }
//    var passwordCheck by remember { mutableStateOf(false) }
//
//    var email by remember { mutableStateOf("") }
//    var emailCheck by remember { mutableStateOf(false) }
//
//    var confirmPassword by remember { mutableStateOf("") }
//    var confirmPasswordCheck by remember { mutableStateOf(false) }

    var isPasswordVisible by remember { mutableStateOf(false) }
    var isSignupScreen by remember { mutableStateOf(false) }
    val usernameIcon = @Composable {
        Icon(
            painter = painterResource(R.drawable.user),
            contentDescription = "Username",
            tint = Color.DarkGray
        )
    }
    val passwordIcon = @Composable {
        Icon(
            painter = painterResource(R.drawable.password),
            contentDescription = "Password",
            tint = Color.DarkGray
        )
    }
    val visibilityIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) painterResource(R.drawable.visibility) else painterResource(R.drawable.visibility_off),
                contentDescription = "",
                tint = Color.DarkGray
            )
        }
    }
    val emailIcon = @Composable {
        Icon(
            painter = painterResource(R.drawable.email),
            contentDescription = "Email",
            tint = Color.DarkGray
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(48.dp)
        ) {
            if (isSignupScreen) {
                IconButton(onClick = {
                    isSignupScreen = false
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App logo",
                alignment = Alignment.Center,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(150.dp)

            )

            Text(
                if (!isSignupScreen) "Login to your account" else "Sign Up",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = state.username.value,
                onValueChange = {
                    state.username.value = it
                    if (state.password.value != "") state.usernameFormatCheck.value = false
                },
                leadingIcon = usernameIcon,
                shape = RoundedCornerShape(25),
                placeholder = { Text("Username") },
                singleLine = true,
                supportingText = {
                    ErrorMessage(failedCheck = state.usernameFormatCheck.value, text = "Invalid format")
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = state.password.value,
                onValueChange = {
                    state.password.value = it
                    if (state.password.value != "") state.passwordFormatCheck.value = true
                },
                leadingIcon = passwordIcon,
                shape = RoundedCornerShape(25),
                placeholder = { Text("Password") },
                singleLine = true,
                trailingIcon = visibilityIcon,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = {
                    ErrorMessage(
                        failedCheck = state.passwordFormatCheck.value,
                        text = "Invalid format"
                    )
                }


            )
            Spacer(modifier = Modifier.height(10.dp))
            if (!isSignupScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RememberCheckbox("Remember me", {}, false)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {
                    viewModel.processIntent(LoginMviIntents.LogIn(state.username.value, state.password.value))
                    if (state.loginSuccess.value) {
//                    usernameCheck = validateInput(username, "USERNAME")
//                    passwordCheck = validateInput(password, "PASSWORD")
//                    if (!usernameCheck && !passwordCheck) {
//                        val foundUser =
//                            userEntries.filter { it.username == username && it.password == password }
//                        success.value = foundUser.isNotEmpty()
//
//                        if (success.value) {
                            backStack.add(Home)
                            backStack.removeRange(0, backStack.indexOf(Home))
                        }
//                    }
                }, modifier = Modifier.width(280.dp)) {
                    Text("Log in")
                }
                Spacer(modifier = Modifier.height(200.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don't have an account?", color = Color.White)
                    TextButton(onClick = { isSignupScreen = true }) {
                        Text("Sign Up")
                    }
                }
            } else {
                OutlinedTextField(
                    value = state.confirmPassword.value,
                    onValueChange = {
                        state.confirmPassword.value = it
                        if (state.confirmPassword.value != "") state.confirmPasswordCheck.value = true

                    },
                    leadingIcon = passwordIcon,
                    shape = RoundedCornerShape(25),
                    placeholder = { Text("Confirm password") },
                    singleLine = true,
                    trailingIcon = visibilityIcon,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = {
                        ErrorMessage(
                            failedCheck = state.passwordFormatCheck.value,
                            text = "Invalid format"
                        )
                    }

                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.email.value,
                    onValueChange = {
                        state.email.value = it
                        if (state.email.value != "") state.emailFormatCheck.value = true
                    },
                    leadingIcon = emailIcon,
                    shape = RoundedCornerShape(25),
                    placeholder = { Text("Email") },
                    singleLine = true,
                    supportingText = {
                        ErrorMessage(
                            failedCheck = state.emailFormatCheck.value,
                            text = "Invalid format"
                        )
                    }

                )

                Spacer(modifier = Modifier.height(128.dp))
                Button(onClick = {
//                    usernameCheck = validateInput(username, "USERNAME")
//                    passwordCheck = validateInput(password, "PASSWORD")
//                    confirmPasswordCheck =
//                        validateInput(confirmPassword, "PASSWORD") && !(password == confirmPassword)
//                    emailCheck = validateInput(email, "EMAIL")
//                    if (!usernameCheck && !passwordCheck && !confirmPasswordCheck && !emailCheck) {
//                        userEntries.add(UserEntry(username, password, email))
//                        isSignupScreen = false
//                        username = ""
//                        password = ""
//                    } else {
//                        if (usernameCheck) {
//                            username = ""
//                        }
//                        if (passwordCheck || confirmPasswordCheck) {
//                            password = ""
//                            confirmPassword = ""
//                        }
//                        if (emailCheck) {
//                            email = ""
//                        }
//                    }
                    viewModel.processIntent(LoginMviIntents.SignUp(state.username.value, state.password.value, state.confirmPassword.value, state.email.value))
                }, modifier = Modifier.width(280.dp)) {
                    Text("Sign Up")
                }
            }

        }
    }
}