package com.apero.minhnt1

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.apero.minhnt1.screens.HomeScreen
import com.apero.minhnt1.screens.playlist.PlaylistScreen
import com.apero.minhnt1.screens.login.LoginScreen
import com.apero.minhnt1.screens.login.LoginViewModel
import com.apero.minhnt1.screens.library.LibraryScreen
import com.apero.minhnt1.screens.library.Song
import com.apero.minhnt1.screens.profile.ProfileScreen
import com.apero.minhnt1.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    private val viewModel: LoginViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
//        val requestPermissionLauncher =
//            registerForActivityResult(RequestPermission()
//            ) { isGranted: Boolean ->
//                if (isGranted) {
//                    // Permission is granted. Continue the action or workflow in your
//                    // app.
//                } else {
//                    // Explain to the user that the feature is unavailable because the
//                    // feature requires a permission that the user has denied. At the
//                    // same time, respect the user's decision. Don't link to system
//                    // settings in an effort to convince the user to change their
//                    // decision.
//                }
//            }
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(2000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            val backStack = remember { mutableStateListOf<Screen>(Login) }
            AppTheme {
                Scaffold(
                    bottomBar = {
                        if (viewModel.state.collectAsState().value.loginSuccess.value) {
                            NavigationBar {
                                var currSelection: Screen = Home
                                destinations.forEach { destination ->

                                    var isSelected = destination == currSelection
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = {
                                            //backStack.clear()
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
                ) {
                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        entryProvider = entryProvider {
                            entry<Login> {
                                LoginScreen(viewModel = viewModel, backStack = backStack)
                            }
                            entry<Home> {
                                HomeScreen(backStack)
                            }
                            entry<Information> {
                                ProfileScreen()
                            }
                            entry<Library> {key ->
                                LibraryScreen(applicationContext, backStack = backStack) {

                                }
                            }
                            entry<Playlist> {
                                PlaylistScreen(applicationContext)
                            }
                        }
                    )
                }
            }
        }
    }
}

sealed interface Screen {
    val name: String
    val icon: Int
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

private val destinations: List<Screen> = listOf(Home, Library, Playlist)







data class DropdownItems(val text: String, val icon: Int)

data class User(
    var username: String = "",
    var password: String = "",
    var email: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var universityName: String = "",
    var selfDescription: String = ""
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

