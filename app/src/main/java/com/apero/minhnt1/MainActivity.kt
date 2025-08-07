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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.apero.minhnt1.screens.HomeScreen
import com.apero.minhnt1.screens.library.LibraryScreen
import com.apero.minhnt1.screens.login.LoginScreen
import com.apero.minhnt1.screens.login.LoginViewModel
import com.apero.minhnt1.screens.playlist.PlaylistScreen
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

        // Boolean vars to identify if a screen was already launched,
        // to triggering on-launch functions
        var isLibraryAlreadyLaunched = false
        var isPlaylistAlreadyLaunched = false
        val sharedPref = getPreferences(MODE_PRIVATE)
        if (!sharedPref.contains("isLoggedIn"))
            sharedPref.edit {
                putBoolean(
                    "isLoggedIn",
                    false
                )
            }
        if (!sharedPref.contains("isRememberMeChecked"))
            sharedPref.edit {
                putBoolean(
                    "isRememberMeChecked",
                    false
                )
            }
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(1000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()
        setContent {
            var shouldShowNavBar = remember { mutableStateOf(false) }
            val backStack = remember {
                mutableStateListOf<Screen>(
                    if (sharedPref.getBoolean("isLoggedIn", true)
                        && sharedPref.getBoolean("isRememberMeChecked", true)
                    ) Home else Login
                )
            }
            AppTheme {
                Scaffold(
                    bottomBar = {
                        if (shouldShowNavBar.value) {
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
                                LoginScreen(
                                    viewModel = viewModel,
                                    backStack = backStack,
                                    context = applicationContext
                                )
                            }
                            entry<Home> {
                                sharedPref.edit {
                                    putBoolean(
                                        "isLoggedIn",
                                        true
                                    ).putBoolean(
                                        "isRememberMeChecked",
                                        viewModel.state.value
                                            .isRememberMeChecked.value
                                    ).apply()
                                }
                                shouldShowNavBar.value = true
                                HomeScreen(backStack)

                            }
                            entry<Information> {
                                ProfileScreen(context = applicationContext)
                            }
                            entry<Library> { key ->

                                LibraryScreen(
                                    applicationContext,
                                    backStack = backStack,
                                    isAlreadyLaunched = isLibraryAlreadyLaunched
                                )
                                if (!isLibraryAlreadyLaunched) {
                                    isLibraryAlreadyLaunched = true
                                }
                            }
                            entry<Playlist> {
                                PlaylistScreen(
                                    applicationContext,
                                    isAlreadyLaunched = isPlaylistAlreadyLaunched
                                )
                                if (!isPlaylistAlreadyLaunched) {
                                    isPlaylistAlreadyLaunched = true
                                }
                            }
                        }
                    )
//                    if (sharedPref.getBoolean("isLoggedIn", true)
//                        && sharedPref.getBoolean("isRememberMeChecked", true)
//                    ) {
//                        backStack.add(Home)
//                    }

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

object Username {
    var value = ""
}

private val destinations: List<Screen> = listOf(Home, Library, Playlist)

data class DropdownItems(val text: String, val icon: Int)

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

