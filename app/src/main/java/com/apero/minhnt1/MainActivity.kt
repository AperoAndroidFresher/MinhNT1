package com.apero.minhnt1

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.screens.HomeScreen
import com.apero.minhnt1.screens.library.LibraryMviState
import com.apero.minhnt1.screens.library.LibraryScreen
import com.apero.minhnt1.screens.login.LoginScreen
import com.apero.minhnt1.screens.login.LoginViewModel
import com.apero.minhnt1.screens.player.MusicPlayerScreen
import com.apero.minhnt1.screens.playlist.PlaylistScreen
import com.apero.minhnt1.screens.profile.ProfileScreen
import com.apero.minhnt1.ui.theme.AppTheme
import com.apero.minhnt1.utility.millisToDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MediaManager {
    var libraryState: LibraryMviState? = null
    var currentSongIndex: MutableState<Int> = mutableIntStateOf(0)
    var currentDuration: MutableState<Float> = mutableFloatStateOf(0f)
    var isExisting: MutableState<Boolean> = mutableStateOf(false)
    var isPlaying: MutableState<Boolean> = mutableStateOf(false)
    var MediaPlayer: MediaPlayer? = null
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    private var musicService: MediaPlaybackService? = null
    private var isBound = false
    private val viewModel: LoginViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        MediaManager.MediaPlayer = MediaPlayer()
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
        applicationContext.bindService(Intent(applicationContext, MediaPlaybackService::class.java), connection, Context.BIND_AUTO_CREATE)
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
                        Column() {
                            if (MediaManager.isExisting.value) {
                                LinearProgressIndicator(
                                    progress =
                                        { MediaManager.currentDuration.value / getCurrentSong().duration.toFloat() },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                BottomAppBar(
                                    actions = {
                                        if (MediaManager.isExisting.value) {
                                            IconButton(onClick = {
                                                MediaManager.isPlaying.value =
                                                    !MediaManager.isPlaying.value
                                                if (MediaManager.isPlaying.value) {
                                                    MediaManager.MediaPlayer?.start()
                                                } else {
                                                    MediaManager.MediaPlayer?.pause()
                                                }
                                            }) {
                                                Icon(
                                                    painter = painterResource(if (MediaManager.isPlaying.value) R.drawable.pause else R.drawable.play),
                                                    contentDescription = "Play/pause"
                                                )
                                            }
                                            Text(
                                                "Now Playing",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.headlineSmall,
                                                modifier = Modifier.width(230.dp)
                                            )
                                            Text(
                                                "03:00",
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.headlineSmall
                                            )
                                            IconButton(onClick = {
                                                MediaManager.isExisting.value = false
                                                MediaManager.MediaPlayer?.stop()
                                            }) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.exit),
                                                    contentDescription = "Exit"
                                                )
                                            }
                                        }
                                    }
                                )
                            }
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
                                    isAlreadyLaunched = isLibraryAlreadyLaunched,
                                    playbackService = musicService
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
                            entry<MusicPlayer> {
                                MusicPlayerScreen()
                            }
                        }
                    )
                }
            }
        }
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlaybackService.MusicBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(connection)
            isBound = false
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

data object MusicPlayer : Screen {
    override val name = "MusicPlayer"
    override val icon = 0
}

object Username {
    var value = ""
}

val destinations: List<Screen> = listOf(Home, Library, Playlist)

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

@Composable
fun MusicPlayerBar(backStack: SnapshotStateList<Screen>) {
    var isPlaying = remember { mutableStateOf(true) }
    BottomAppBar(
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    painter = painterResource(if (isPlaying.value) R.drawable.pause else R.drawable.play),
                    contentDescription = "Play / pause"
                )
            }
            Text(
                text = getCurrentSong().title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .width(230.dp)
                    .basicMarquee()
                    .clickable {
                        backStack.add(MusicPlayer)
                    }
            )
            Text(
                text = millisToDuration(getCurrentSong().duration),
                maxLines = 1,
                style = MaterialTheme.typography.headlineSmall
            )
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    painter = painterResource(R.drawable.email),
                    contentDescription = "Localized description",
                )
            }
        }
    )
}

fun getCurrentSong() : Song {
    return MediaManager.libraryState!!.songLibrary[MediaManager.currentSongIndex.value]
}



