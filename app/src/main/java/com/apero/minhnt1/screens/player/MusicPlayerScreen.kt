package com.apero.minhnt1.screens.player

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.apero.minhnt1.MediaManager
import com.apero.minhnt1.MediaPlaybackService
import com.apero.minhnt1.R
import com.apero.minhnt1.Screen
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.getCurrentSong


@Composable
fun MusicPlayerScreen() {

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun CenterAlignedTopAppBarExample(
    backStack: SnapshotStateList<Screen> = mutableStateListOf(),
    cover: Bitmap? = null
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var progressPosition = remember { mutableFloatStateOf(0.2f) }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "Now Playing",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { backStack.removeLastOrNull() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.exit),
                            contentDescription = "Exit"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) {
        val painter = if (cover != null) {
            rememberAsyncImagePainter(model = cover)
        } else {
            painterResource(R.drawable.cover_1)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .statusBarsPadding()
                .padding(64.dp)
                .fillMaxSize()

        ) {

            Box(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .size(256.dp)
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Song cover",
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterStart,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sample",
                style = MaterialTheme.typography.headlineMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, end = 4.dp)
                    .fillMaxWidth().basicMarquee()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Sample",
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 4.dp, end = 4.dp)
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = (MediaManager.currentDuration.value / getCurrentSong().duration.toFloat()),
                onValueChange = {MediaManager.currentDuration.value = it
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.shuffle),
                        contentDescription = "Shuffle"
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        painter = painterResource(R.drawable.previous),
                        contentDescription = "Previous song"
                    )
                }
                IconButton(onClick = {
                    MediaManager.isPlaying.value = !MediaManager.isPlaying.value
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
                IconButton(onClick = {  }) {
                    Icon(
                        painter = painterResource(R.drawable.skip),
                        contentDescription = "Next song"
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(R.drawable.loop),
                        contentDescription = "Loop playlist"
                    )
                }
            }
        }
    }
}

private fun getNextLocalSong(song: Song) : Song? {
    val currIndex = MediaManager.libraryState!!.songLibrary.indexOf(song)
    if (currIndex == MediaManager.libraryState!!.songLibrary.size - 1) {
        return null
    } else {
        return MediaManager.libraryState!!.songLibrary[currIndex + 1]
    }
}

private fun getNextRemoteSong(song: Song) : Song? {
    val currIndex = MediaManager.libraryState!!.remoteSongLibrary.indexOf(song)
    if (currIndex == MediaManager.libraryState!!.remoteSongLibrary.size - 1) {
        return null
    } else {
        return MediaManager.libraryState!!.remoteSongLibrary[currIndex + 1]
    }
}