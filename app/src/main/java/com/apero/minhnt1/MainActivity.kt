package com.apero.minhnt1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apero.minhnt1.ui.theme.MinhNT1Theme
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinhNT1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(4.dp)) {
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
                    .padding(top = 20.dp, start = 54.dp),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .weight(0.1f)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    modifier = Modifier.padding(top = Dp(20f))
                )
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
                    description = "Your name here..."
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
                    description = "Your phone number..."
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
                description = "Your university number..."
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
                description = "Enter a description about yourself...",
                cornerMod = 15,
                singleLine = false
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { },
            modifier = Modifier
                .height(50.dp)
                .width(140.dp),
            shape = RoundedCornerShape(30)
        ) {
            Text(text = "Submit")
        }
    }

}

@Composable
//@Preview(showBackground = true)
fun TextFieldComponent(

    text: String = "Sample",
    description: String = "Sample",
    cornerMod: Int = 30,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column {
        Text(text = text.uppercase(), fontSize = 12.sp)
        Spacer(modifier = modifier.height(Dp(8f)))
        Row {
            var textFieldValue by remember() {
                mutableStateOf(TextFieldValue(description ))
            }

            OutlinedTextField(
                value = textFieldValue,
                singleLine = singleLine,
                onValueChange = { newText ->
                    textFieldValue = newText
                },
                shape = RoundedCornerShape(cornerMod),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                modifier = modifier
                    .fillMaxSize()
                    .heightIn(1.dp)
            )
        }

    }
}

//@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MinhNT1Theme {
        Greeting("Android")
    }
}