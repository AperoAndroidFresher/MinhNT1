package com.apero.minhnt1.screens.login

import android.content.Context
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.apero.minhnt1.Home
import com.apero.minhnt1.R
import com.apero.minhnt1.RememberCheckbox
import com.apero.minhnt1.Screen
import com.apero.minhnt1.database.AppDatabase
import com.apero.minhnt1.database.user.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@Composable
//@Preview(showBackground = true)
fun LoginScreen(
    context: Context,
    viewModel: LoginViewModel,
    backStack: SnapshotStateList<Screen>,
) {
    val userDao = AppDatabase.getDatabase(context = context).userDao()
    val state by viewModel.state.collectAsStateWithLifecycle()

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
        IconButton(onClick = { state.isPasswordVisible.value = !state.isPasswordVisible.value }) {
            Icon(
                if (state.isPasswordVisible.value) painterResource(R.drawable.visibility) else painterResource(
                    R.drawable.visibility_off
                ),
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
            if (state.isSignupScreen.value) {
                IconButton(onClick = {
                    state.isSignupScreen.value = false
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
                if (!state.isSignupScreen.value) "Login to your account" else "Sign Up",
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
                    ErrorMessage(
                        passedCheck = state.usernameFormatCheck.value,
                        text = "Invalid format"
                    )
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
                visualTransformation = if (state.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                supportingText = {
                    ErrorMessage(
                        passedCheck = state.passwordFormatCheck.value,
                        text = "Invalid format"
                    )
                }


            )
            Spacer(modifier = Modifier.height(10.dp))
            if (!state.isSignupScreen.value) {
                var isRememberMeChecked = remember { mutableStateOf(true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RememberCheckbox(
                        "Remember me",
                        { isRememberMeChecked.value = !isRememberMeChecked.value },
                        isRememberMeChecked.value
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            val retrievedUser =
                                userDao.getSpecificUserForLogin(
                                    state.username.value,
                                    state.password.value
                                )
                            if (retrievedUser.isNotEmpty()) {
                                viewModel.processIntent(
                                    LoginMviIntents.LogIn(
                                        state.username.value,
                                        state.password.value,
                                        isRememberMeChecked.value
                                    )
                                )
                                backStack.add(Home)
                                backStack.removeRange(0, backStack.indexOf(Home))
                            }
                        }
                    }
                }, modifier = Modifier.width(280.dp)) {
                    Text("Log in")
                }
                Spacer(modifier = Modifier.height(120.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Don't have an account?", color = Color.White)
                    TextButton(onClick = { state.isSignupScreen.value = true }) {
                        Text("Sign Up")
                    }
                }
            } else {
                OutlinedTextField(
                    value = state.confirmPassword.value,
                    onValueChange = {
                        state.confirmPassword.value = it
                        if (state.confirmPassword.value != "") state.confirmPasswordCheck.value =
                            true

                    },
                    leadingIcon = passwordIcon,
                    shape = RoundedCornerShape(25),
                    placeholder = { Text("Confirm password") },
                    singleLine = true,
                    trailingIcon = visibilityIcon,
                    visualTransformation = if (state.isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    supportingText = {
                        ErrorMessage(
                            passedCheck = state.passwordFormatCheck.value,
                            text = "Invalid format"
                        )
                    }

                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.emailAddress.value,
                    onValueChange = {
                        state.emailAddress.value = it
                        if (state.emailAddress.value != "") state.emailFormatCheck.value = true
                    },
                    leadingIcon = emailIcon,
                    shape = RoundedCornerShape(25),
                    placeholder = { Text("Email") },
                    singleLine = true,
                    supportingText = {
                        ErrorMessage(
                            passedCheck = state.emailFormatCheck.value,
                            text = "Invalid format"
                        )
                    }

                )

                Spacer(modifier = Modifier.height(64.dp))
                Button(onClick = {
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            userDao.insert(
                                User(
                                    username = state.username.value,
                                    password = state.password.value,
                                    email = state.emailAddress.value
                                )
                            )
                            viewModel.processIntent(
                                LoginMviIntents.SignUp(
                                    state.username.value,
                                    state.password.value,
                                    state.confirmPassword.value,
                                    state.emailAddress.value
                                )
                            )
                        }
                    }
                }, modifier = Modifier.width(280.dp)) {
                    Text("Sign Up")
                }
            }

        }
    }
}


@Composable
fun ErrorMessage(passedCheck: Boolean, text: String) {
    if (!passedCheck) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            color = MaterialTheme.colorScheme.error
        )
    }
}