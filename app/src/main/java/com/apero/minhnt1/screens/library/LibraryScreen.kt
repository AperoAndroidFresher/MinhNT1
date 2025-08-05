package com.apero.minhnt1.screens.library

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.apero.minhnt1.DropdownItems
import com.apero.minhnt1.Playlist
import com.apero.minhnt1.R
import com.apero.minhnt1.Screen
import com.apero.minhnt1.database.AppDatabase
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.database.song.SongDao
import com.apero.minhnt1.utility.convertBitmapToImage
import com.apero.minhnt1.utility.millisToDuration
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Date

object IncomingSong {
    var Song = Song()
    var showAddToPlaylistPopup = false
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
//@Preview(showBackground = true)
fun LibraryScreen(
    context: Context,
    viewModel: LibraryViewModel = viewModel(),
    backStack: SnapshotStateList<Screen>,
    isAlreadyLaunched: Boolean,
    onClick: () -> Unit = {}
) {
    val songDao = AppDatabase.getDatabase(context = context).songDao()
    val resolver = context.contentResolver
    val state by viewModel.state.collectAsStateWithLifecycle()

    var isList by remember { mutableStateOf(false) }
    val mediaPermission = rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)
    if (mediaPermission.status.isGranted) {
        var isLocal by remember { mutableStateOf(true) }
        //var library = remember { mutableStateListOf<Song>() }
        if (!isAlreadyLaunched) {
            populateMusicLibrary(resolver, songDao)
            state.songLibrary = getMusicLibrary(songDao)
        }

        val lazyListState = rememberLazyListState()
        val lazyGridState = rememberLazyGridState()
        val dropdownItems = remember { mutableStateListOf<DropdownItems>() }
        dropdownItems.add(DropdownItems("Add to playlist", R.drawable.add_song))
        dropdownItems.add(DropdownItems("Remove from playlist", R.drawable.remove))
        dropdownItems.add(DropdownItems("Share", R.drawable.share))

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Library",
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(start = 96.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                IconButton(onClick = {
                    viewModel.processIntent(LibraryMviIntents.SwitchView)
                }, enabled = true) {
                    Icon(
                        painter = painterResource(id = if (isList) R.drawable.grid else R.drawable.hamburger_icon),
                        contentDescription = "Grid",
                        modifier = Modifier.weight(0.1f),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                IconButton(onClick = {

                }, enabled = true) {
                    Icon(
                        painter = painterResource(id = R.drawable.sort_descending),
                        contentDescription = "Sort",
                        modifier = Modifier.weight(0.1f),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Spacer(Modifier
                .height(10.dp)
                .background(Color.Black))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Black),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { isLocal = true }, modifier = Modifier.size(120.dp, 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        contentColor = if (isLocal) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text("Local")
                }
                Button(
                    onClick = { isLocal = false }, modifier = Modifier.size(120.dp, 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!isLocal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                        contentColor = if (!isLocal) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text("Remote")
                }
            }
            Spacer(Modifier
                .height(10.dp)
                .background(Color.Black))
            if (state.isList.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(Black),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = lazyListState
                ) {
                    var counter = 0
                    itemsIndexed(state.songLibrary) { index, item ->
//                SongEntry(
//                    cover = state.playlist[index].cover,
//                    title = state.playlist[index].title,
//                    author = state.playlist[index].author,
//                    length = state.playlist[index].length
//                )
                        counter++
                        Log.d("ListItem Count", counter.toString())

                        val cover = remember(state.songLibrary[index].songID) {
                            convertBitmapToImage(state.songLibrary[index].cover, context)
                        }

                        val painter = if (cover != null) {
                            rememberAsyncImagePainter(model = cover)
                        } else {
                            painterResource(R.drawable.cover_1)
                        }

                        Row(
                            modifier = Modifier
                                .background(Black)
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painter,
                                contentDescription = "Song cover",
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.CenterStart,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10))
                            )
                            Column(
                                modifier = Modifier.weight(0.7f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    state.songLibrary[index].title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    state.songLibrary[index].artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                millisToDuration(
                                    state.songLibrary[index].duration
                                ),
                                modifier = Modifier.weight(0.15f),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White,
                                textAlign = TextAlign.End
                            )
                            var isDropdownMenuVisible by remember { mutableStateOf(false) }
                            Box {
                                IconButton(onClick = {
                                    isDropdownMenuVisible = true
                                }, enabled = true) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.vertical_dots),
                                        contentDescription = "Grid",
                                        tint = Color.White
                                    )
                                    DropdownMenu(
                                        expanded = isDropdownMenuVisible,
                                        onDismissRequest = { isDropdownMenuVisible = false },
                                        modifier = Modifier.background(Black)
                                    ) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dropdownItems[0].text,
                                                    color = Color.White
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(id = dropdownItems[0].icon),
                                                    contentDescription = "Add to playlist",
                                                    tint = Color.White
                                                )
                                            },
                                            onClick = {
                                                isDropdownMenuVisible = false
                                                IncomingSong.Song = state.songLibrary[index]
                                                IncomingSong.showAddToPlaylistPopup = true
                                                backStack.add(Playlist)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dropdownItems[1].text,
                                                    color = Color.White
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(id = dropdownItems[1].icon),
                                                    contentDescription = "Remove",
                                                    tint = Color.White
                                                )
                                            },
                                            onClick = {
                                                runBlocking {
                                                    withContext(Dispatchers.IO) {
                                                        songDao.delete(item)
                                                    }
                                                }
                                                state.songLibrary.removeAt(index)
                                                isDropdownMenuVisible = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dropdownItems[2].text,
                                                    color = Color.White
                                                )
                                            },
                                            leadingIcon = {
                                                Icon(
                                                    painter = painterResource(id = dropdownItems[2].icon),
                                                    contentDescription = "Share",
                                                    tint = Color.White
                                                )
                                            },
                                            onClick = {
                                                val sendIntent: Intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(
                                                        Intent.EXTRA_STREAM,
                                                        state.songLibrary[index].cover
                                                    )
                                                    type = "audio/*"
                                                }
                                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                                val shareIntent =
                                                    Intent.createChooser(sendIntent, null)
                                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(context, shareIntent, null)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .background(Black),
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var counter = 0
                    itemsIndexed(state.songLibrary) { index, item ->
//                SongEntryVertical(
//                    cover = state.playlist[index].cover,
//                    title = state.playlist[index].title,
//                    author = state.playlist[index].author,
//                    length = state.playlist[index].length
//                )
                        counter++
                        Log.d("LVG Count", counter.toString())
                        val cover = remember(state.songLibrary[index].songID) {
                            convertBitmapToImage(state.songLibrary[index].cover, context)
                        }

                        val painter = if (cover != null) {
                            rememberAsyncImagePainter(model = cover)
                        } else {
                            painterResource(R.drawable.cover_1)
                        }

                        var isDropdownMenuVisible by remember { mutableStateOf(false) }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(10))
                            ) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Song cover",
                                    contentScale = ContentScale.Crop,
                                    alignment = Alignment.CenterStart,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(onClick = {

                                }, enabled = true) {
                                    Box {
                                        IconButton(onClick = {
                                            isDropdownMenuVisible = true
                                        }, enabled = true) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.vertical_dots),
                                                contentDescription = "Grid",
                                                tint = Color.White
                                            )
                                            DropdownMenu(
                                                expanded = isDropdownMenuVisible,
                                                onDismissRequest = {
                                                    isDropdownMenuVisible = false
                                                },
                                                modifier = Modifier.background(Black)
                                            ) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            dropdownItems[0].text,
                                                            color = Color.White
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(id = dropdownItems[0].icon),
                                                            contentDescription = "Add to playlist",
                                                            tint = Color.White
                                                        )
                                                    },
                                                    onClick = {
                                                        isDropdownMenuVisible = false
                                                        IncomingSong.Song = state.songLibrary[index]
                                                        IncomingSong.showAddToPlaylistPopup = true
                                                        backStack.add(Playlist)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            dropdownItems[1].text,
                                                            color = Color.White
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(id = dropdownItems[0].icon),
                                                            contentDescription = "Remove",
                                                            tint = Color.White
                                                        )
                                                    },
                                                    onClick = {
                                                        runBlocking {
                                                            withContext(Dispatchers.IO) {
                                                                songDao.delete(item)
                                                            }
                                                        }
                                                        state.songLibrary.removeAt(index)
                                                        isDropdownMenuVisible = false
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            dropdownItems[2].text,
                                                            color = Color.White
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            painter = painterResource(id = dropdownItems[1].icon),
                                                            contentDescription = "Share",
                                                            tint = Color.White
                                                        )
                                                    },
                                                    onClick = {
                                                        val sendIntent: Intent = Intent().apply {
                                                            action = Intent.ACTION_SEND
                                                            putExtra(
                                                                Intent.EXTRA_STREAM,
                                                                state.songLibrary[index].cover
                                                            )
                                                            type = "audio/*"
                                                        }
                                                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                        val shareIntent =
                                                            Intent.createChooser(sendIntent, null)
                                                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        startActivity(context, shareIntent, null)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.songLibrary[index].title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                state.songLibrary[index].artist,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                millisToDuration(
                                    state.songLibrary[index].duration
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )

                        }
                    }
                }
            }
        }


    } else {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(8.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (mediaPermission.status.shouldShowRationale) {
                "Loading songs from your local device requires access to your songs. " +
                        "Please grant the relevant permission to use this feature."
            } else {
                "Please grant access to your local storage to use this feature."
            }
            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { mediaPermission.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }

}

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

private fun populateMusicLibrary(
    resolver: ContentResolver?,
    songDao: SongDao
) {
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.DATA
    )
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
    val selection =
        "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val cursor = resolver?.query(uri, projection, selection, null, null)
    cursor?.use {
        when {
            !cursor.moveToFirst() -> {
                Log.e("LibraryScreen", "No media found")
            }
            else -> {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                do {
                    val title = it.getString(titleColumn)
                    val artist = it.getString(artistColumn)
                    val duration = it.getLong(durationColumn)
                    val path = it.getString(dataColumn)
                    val id = it.getString(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        it.getString(idColumn).toLong()
                    )
                    val newSong = Song(
                        title = title,
                        artist = artist,
                        duration = duration,
                        path = path,
                        cover = contentUri,
                        songID = id.toInt()
                    )
                    runBlocking {
                        withContext(Dispatchers.IO) {
                            songDao.insert(newSong)
                        }
                    }
                } while (cursor.moveToNext())
            }
        }
    }
}

private fun getMusicLibrary(songDao: SongDao): SnapshotStateList<Song> {
    val library: MutableList<Song>
    runBlocking {
        withContext(Dispatchers.IO) {
            library = songDao.getAll()
        }
    }
    val stateLibrary = mutableStateListOf<Song>()
    for (song in library) stateLibrary.add(song)
    return stateLibrary
}





