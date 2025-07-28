package com.apero.minhnt1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.apero.minhnt1.screens.HomeScreen
import com.apero.minhnt1.screens.LibraryScreen
import com.apero.minhnt1.screens.LoginScreen
import com.apero.minhnt1.screens.PlaylistScreen
import com.apero.minhnt1.screens.ProfileScreen
import com.apero.minhnt1.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()

        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(3000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            val backStack = remember { mutableStateListOf<Screen>(Login) }
            var successfulLogin = remember { mutableStateOf(false) }
            AppTheme {
                Scaffold(
                    bottomBar = {
                        if (successfulLogin.value) {
                            NavigationBar {
                                var currSelection: Screen = Home
                                destinations.forEach { destination ->

                                    var isSelected = destination == currSelection
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            backStack.clear()
                                            backStack.add(destination)
                                            currSelection = destination
                                        },
                                        label = {
                                            Text(destination.name)
                                        },
                                        icon = {
                                            Icon(
                                                painterResource(destination.icon),
                                                contentDescription = destination.name
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { _ ->
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<Login> {
                                LoginScreen(backStack = backStack, success = successfulLogin)
                            }
                            entry<Home> {
                                HomeScreen(backStack)
                            }
                            entry<Information> {
                                ProfileScreen(backStack)
                            }
                            entry<Library> {
                                LibraryScreen(backStack)
                            }
                            entry<Playlist> {
                                PlaylistScreen(backStack)
                            }
                        }
                    )
                }
            }
        }
    }
}

//enum class Screen {
//    LOGIN, SIGNUP, HOME, PLAYLIST, INFORMATION, LIBRARY
//}

sealed interface Screen {
    open val name: String
    open val icon: Int
}

data object Login : Screen {
    override val name = "Login"
    override val icon = 0
}

data object Home : Screen {
    override val name = "Home"
    override val icon = R.drawable.home
}

data object Information : Screen {
    override val name = "Information"
    override val icon = 0
}

data object Playlist : Screen {
    override val name = "Playlist"
    override val icon = R.drawable.playlist
}

data object Library : Screen {
    override val name = "Library"
    override val icon = R.drawable.music
}


//data object Login
//data object Home
//data object Information
//data object Playlist
//data object Library
var user: User = User()
private val destinations: List<Screen> = listOf(Home, Library, Playlist)


@Composable
//@Preview(showBackground = true)
fun SongEntry(
    cover: Int = R.drawable.cover_1,
    title: String = "Sample",
    author: String = "Sample",
    length: String = "00:00"
) {
    Row(
        modifier = Modifier
            .background(Black)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = cover),
            contentDescription = "Song cover",
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterStart,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10))
        )
        Column(modifier = Modifier.weight(0.7f), verticalArrangement = Arrangement.Center) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                author,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
        Text(
            length,
            modifier = Modifier.weight(0.15f),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.End
        )
        IconButton(onClick = {

        }, enabled = true) {
            Icon(
                painter = painterResource(id = R.drawable.vertical_dots),
                contentDescription = "Grid",
                modifier = Modifier.weight(0.05f),
                tint = Color.White
            )
        }
    }
}

@Composable
//@Preview(showBackground = false)
fun SongEntryVertical(
    cover: Int = R.drawable.cover_1,
    title: String = "Sample",
    author: String = "Sample",
    length: String = "00:00"
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(10))
        ) {
            Image(
                painter = painterResource(id = cover),
                contentDescription = "Song cover",
                contentScale = ContentScale.Crop,
                alignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxSize()
            )
            IconButton(onClick = {

            }, enabled = true) {
                Icon(
                    painter = painterResource(R.drawable.vertical_dots),
                    contentDescription = "Grid",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(60.dp),
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            author,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(length, style = MaterialTheme.typography.bodyMedium, color = Color.White)

    }
}


data class Song(val cover: Int, val title: String, val author: String, val length: String)


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
    failedCheck: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = false,
    keyboardController: KeyboardActions
) {
    Column {
        Text(text = text.uppercase(), fontSize = 12.sp)
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
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardController,
                supportingText = {
                    if (failedCheck) {
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

@Composable
fun ErrorMessage(failedCheck: Boolean, text: String) {
    if (failedCheck) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            color = MaterialTheme.colorScheme.error
        )
    }
}

fun validateInput(text: String, source: String): Boolean {
    when (source) {
        "PHONE NUMBER" -> {
            return if (text.isDigitsOnly()) false else true
        }

        "NAME", "UNIVERSITY NAME" -> {
            return if (text.matches(Regex("^[A-z\\s]*$"))) false else true
        }

        "PASSWORD", "USERNAME" -> {
            return if (text.matches(Regex("^[A-z0-9]*\$"))) false else true
        }

        "EMAIL" -> {
            return if (text.matches(Regex("^[A-z0-9._-]+@(apero.vn)"))) false else true
        }
    }
    return false
}

fun switchIcons(isGrid: Boolean): Int {
    if (isGrid) {
        return R.drawable.grid
    } else return R.drawable.hamburger_icon
}


data class DropdownItems(val text: String, val icon: Int)

data class User(
    var name: String = "",
    var phoneNumber: String = "",
    var universityName: String = "",
    var selfDescription: String = ""
)

data class UserEntry(
    var username: String = "",
    var password: String = "",
    var email: String = ""
)

@Composable
fun RememberCheckbox(
    label: String,
    onCheckChanged: () -> Unit,
    isChecked: Boolean
) {

    Row(
        modifier = Modifier
            .clickable(
                onClick = onCheckChanged
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isChecked, onCheckedChange = null)
        Spacer(Modifier.size(6.dp))
        Text(label, color = Color.White)
    }
}

