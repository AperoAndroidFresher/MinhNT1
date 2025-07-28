package com.apero.minhnt1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.apero.minhnt1.R
import com.apero.minhnt1.Screen
import com.apero.minhnt1.TextFieldComponent
import com.apero.minhnt1.user
import com.apero.minhnt1.validateInput
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(backStack: SnapshotStateList<Screen>) {
    var editable by remember { mutableStateOf(false) }
    var editButtonClickable by remember { mutableStateOf(true) }
    var revealSubmit by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .statusBarsPadding()
    ) {
        Row(modifier = Modifier.height(20.dp)) {}
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(50.dp)
        ) {

            Text(
                text = "MY INFORMATION",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .padding(start = 54.dp),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .weight(0.1f)
            ) {
                IconButton(onClick = {
                    editButtonClickable = false
                    editable = true
                    revealSubmit = true
                }, enabled = editButtonClickable) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.starry_night),
            contentDescription = "Starry night",
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterStart,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(1.dp, Black, CircleShape)

        )
        Spacer(Modifier.height(20.dp))
        var name by rememberSaveable { mutableStateOf("") }
        var nameCheck by remember { mutableStateOf(false) }

        var phoneNumber by rememberSaveable { mutableStateOf("") }
        var phoneNumberCheck by remember { mutableStateOf(false) }

        var universityName by rememberSaveable { mutableStateOf("") }
        var universityNameCheck by remember { mutableStateOf(false) }

        var selfDescription by rememberSaveable { mutableStateOf("") }


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
                    description = name,
                    onValueChange = {
                        name = it
                        //nameCheck = validateInput(name, "NAME")
                    },
                    failedCheck = nameCheck,
                    placeholder = "Your name here...",
                    enabled = editable,
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
                    description = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        //phoneNumberCheck = validateInput(phoneNumber, "PHONE NUMBER")
                    },
                    failedCheck = phoneNumberCheck,
                    placeholder = "Your phone number...",
                    enabled = editable,
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
                description = universityName,
                onValueChange = {
                    universityName = it
                    //universityNameCheck = validateInput(universityName, "UNIVERSITY NAME")
                },
                failedCheck = universityNameCheck,
                placeholder = "Your university name...",
                enabled = editable,
                keyboardController = KeyboardActions(
                    onDone = { keyboardController?.hide() })
            )
        }
        Spacer(Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 10.dp)
        ) {
            TextFieldComponent(
                text = "DESCRIBE YOURSELF",
                description = selfDescription,
                cornerMod = 15,
                singleLine = false,
                onValueChange = {
                    selfDescription = it
                },
                placeholder = "Enter a description about yourself...",
                enabled = editable,
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
        if (revealSubmit)
            Button(
                onClick = {
                    // validate user input
                    nameCheck = validateInput(name, "NAME")
                    phoneNumberCheck = validateInput(phoneNumber, "PHONE NUMBER")
                    universityNameCheck = validateInput(universityName, "UNIVERSITY NAME")
                    if (!nameCheck && !phoneNumberCheck && !universityNameCheck) {
                        // To be used if a list of Users is needed and
                        // a certain value (i.e. phoneNumber) should be unique
//                    if (users.any { it.phoneNumber == phoneNumber }) {
//                        var targetUser = users.first { it.phoneNumber == phoneNumber }
//                        targetUser.name = name
//                        targetUser.universityName = universityName
//                        targetUser.selfDescription = selfDescription
//                    }
                        // set user properties
                        user.name = name
                        user.phoneNumber = phoneNumber
                        user.universityName = universityName
                        user.selfDescription = selfDescription

                        // modify interactable objects' states
                        editable = false
                        editButtonClickable = true
                        revealSubmit = false

                        // show success dialog
                        showAlert.value = true
                        Executors.newSingleThreadScheduledExecutor().schedule({
                            showAlert.value = false
                        }, 2, TimeUnit.SECONDS)
                    }
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
