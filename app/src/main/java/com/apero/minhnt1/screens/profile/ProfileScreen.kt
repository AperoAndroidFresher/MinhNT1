package com.apero.minhnt1.screens.profile

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.apero.minhnt1.R
import com.apero.minhnt1.database.AppDatabase
import com.apero.minhnt1.ui.theme.AppTheme
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel(), context: Context) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userDao = AppDatabase.getDatabase(context = context).userDao()
    val keyboardController = LocalSoftwareKeyboardController.current
    var imageUri by remember { mutableStateOf("") }

    AppTheme(darkTheme = state.isDarkMode.value) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(modifier = Modifier.height(20.dp)) {}
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(50.dp)
            ) {
                Box(
                    modifier = Modifier
                        .requiredWidth(64.dp)
                        .height(100.dp)
                        .weight(0.1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        viewModel.processIntent(ProfileMviIntents.ToggleDarkMode)
                    }) {
                        Icon(
                            painter = if (state.isDarkMode.value) painterResource(id = R.drawable.moon)
                            else painterResource(id = R.drawable.sun),
                            contentDescription = "Toggle dark mode",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Text(
                    text = "MY INFORMATION",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.9f)
                        .padding(start = 40.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .weight(0.1f),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        state.editButtonClickable.value = false
                        state.editable.value = true
                        state.revealSubmit.value = true
                    }, enabled = state.editButtonClickable.value) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            var image = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .size(300, 300)
                    .build()
            )
            Box(
                modifier = Modifier
                    .height(180.dp)
                    .width(180.dp)
            ) {
                val imagePicker = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
                    if (uri != null) {
                        imageUri = uri.toString()
                    }
                }
                Image(
                    painter = if (imageUri.isNotEmpty()) image else painterResource(id = R.drawable.starry_night),
                    contentDescription = "Starry night",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.Center)

                )

                IconButton(
                    onClick = {
                        imagePicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                    },
                    enabled = !state.editButtonClickable.value,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                        .align(Alignment.BottomCenter),

                    ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Photo picker",
                        tint = Color.White
                    )
                }

            }
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .weight(1f)
                        .padding(horizontal = 5.dp)
                ) {
                    TextFieldComponent(
                        text = "NAME",
                        description = state.name.value,
                        onValueChange = {
                            state.name.value = it
                        },
                        passedCheck = state.nameFormatCheck.value,
                        placeholder = "Your name here...",
                        enabled = state.editable.value,

                        keyboardController = KeyboardActions(
                            onDone = { keyboardController?.hide() })
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .weight(1f)
                        .padding(horizontal = 5.dp)
                ) {
                    TextFieldComponent(
                        text = "PHONE NUMBER",
                        description = state.phoneNumber.value,
                        onValueChange = {
                            state.phoneNumber.value = it
                        },
                        passedCheck = state.phoneNumberFormatCheck.value,
                        placeholder = "Your phone number...",
                        enabled = state.editable.value,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardController = KeyboardActions(
                            onDone = { keyboardController?.hide() })
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 10.dp)
            ) {
                TextFieldComponent(
                    text = "UNIVERSITY NAME",
                    description = state.university.value,
                    onValueChange = {
                        state.university.value = it
                        //universityNameCheck = validateInput(universityName, "UNIVERSITY NAME")
                    },
                    passedCheck = state.universityFormatCheck.value,
                    placeholder = "Your university name...",
                    enabled = state.editable.value,
                    keyboardController = KeyboardActions(
                        onDone = { keyboardController?.hide() })
                )
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 10.dp)
            ) {
                TextFieldComponent(
                    text = "DESCRIBE YOURSELF",
                    description = state.selfDescription.value,
                    cornerMod = 15,
                    singleLine = false,
                    onValueChange = {
                        state.selfDescription.value = it
                    },
                    placeholder = "Enter a description about yourself...",
                    enabled = state.editable.value,
                    keyboardController = KeyboardActions(
                        onDone = { keyboardController?.hide() })
                )
            }
            Spacer(Modifier.height(20.dp))
            var showAlert = remember { mutableStateOf(false) }
            if (showAlert.value) {

                // Success dialog design
                Dialog(onDismissRequest = { showAlert.value = false }) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.success_icon),
                                contentDescription = "Starry night",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.CenterStart,
                                modifier = Modifier

                                    .size(100.dp)
                                    .clip(CircleShape)

                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Success!",
                                modifier = Modifier
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = Color(0xff2BB673)
                            )
                            Text(
                                text = "Your information has been updated!",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center),
                                textAlign = TextAlign.Center,
                                fontSize = 20.sp
                            )
                        }
                    }
                }

            }
            if (state.revealSubmit.value)
                Button(
                    onClick = {
                        viewModel.processIntent(
                            ProfileMviIntents.Submit(
                                state.name.value,
                                state.phoneNumber.value,
                                state.university.value,
                                state.selfDescription.value
                            )
                        )
                        // show success dialog
                        showAlert.value = true
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            showAlert.value = false
                        }, 2, TimeUnit.SECONDS)

                    },
                    modifier = Modifier
                        .height(50.dp)
                        .width(140.dp),
                    shape = RoundedCornerShape(30)

                ) {
                    Text(text = "Submit")
                }
        }
    }

}

@Composable
//@Preview(showBackground = true)
fun TextFieldComponent(
    modifier: Modifier = Modifier,
    text: String = "Sample",
    placeholder: String,
    description: String = "Sample",
    cornerMod: Int = 30,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    passedCheck: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true,
    keyboardController: KeyboardActions
) {
    Column {
        Text(
            text = text.uppercase(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = modifier.height(Dp(8f)))
        Row {
            OutlinedTextField(
                value = description,
                placeholder = { Text(placeholder, fontSize = 12.sp) },
                singleLine = singleLine,
                onValueChange = onValueChange,
                shape = RoundedCornerShape(cornerMod),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                enabled = enabled,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledContainerColor = MaterialTheme.colorScheme.background
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardController,
                supportingText = {
                    if (!passedCheck) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Invalid input",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                modifier = modifier
                    .fillMaxSize()
                    .heightIn(1.dp)
            )
        }

    }
}
