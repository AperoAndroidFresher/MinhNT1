package com.apero.minhnt1.screens.library

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.apero.minhnt1.DropdownItems
import com.apero.minhnt1.Playlist
import com.apero.minhnt1.R
import com.apero.minhnt1.Screen
import com.apero.minhnt1.database.AppDatabase
import com.apero.minhnt1.database.song.Song
import com.apero.minhnt1.database.song.SongDao
import com.apero.minhnt1.network.ApiClient
import com.apero.minhnt1.utility.convertBitmapToImage
import com.apero.minhnt1.utility.millisToDuration
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalDateTime

object IncomingSong {
    var Song = Song(isLocal = 1)
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
    isAlreadyLaunched: Boolean
) {
    var musicFetchState by remember { mutableIntStateOf(2) }
    val songDao = AppDatabase.getDatabase(context = context).songDao()
    val resolver = context.contentResolver
    val state by viewModel.state.collectAsStateWithLifecycle()
    val mediaPermission = rememberPermissionState(Manifest.permission.READ_MEDIA_AUDIO)
    if (mediaPermission.status.isGranted) {
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
            TopBar(viewModel, state)
            LocalRemoteButtons(state)
            if (state.isLocal.value) {
                if (state.isList.value) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .background(Black)
                            .padding(bottom = 68.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = lazyListState
                    ) {
                        itemsIndexed(state.songLibrary) { index, item ->
                            SongEntry(
                                state = state,
                                context = context,
                                songDao = songDao,
                                backStack = backStack,
                                index = index,
                                item = item,
                                isLocal = true
                            )
                        }
                    }

                } else {
                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .background(Black)
                            .padding(bottom = 68.dp),
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.songLibrary) { index, item ->
                            SongEntryVertical(
                                state = state,
                                context = context,
                                songDao = songDao,
                                backStack = backStack,
                                index = index,
                                item = item,
                                isLocal = true,
                                dropdownItems = dropdownItems
                            )
                        }
                    }
                }
            } else {
                if (!state.hasAlreadyFetchedSongs.value) {
                    viewModel.processIntent(LibraryMviIntents.GetMusicFromRemote)
                    state.hasAlreadyFetchedSongs.value = true
                }

                if (musicFetchState == 2) {
                    Column(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .statusBarsPadding()
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedPreloader(modifier = Modifier.size(200.dp))
                    }
                }
                if (state.remoteSongLibrary.isNotEmpty()) {
                    runBlocking {
                        //val task = async(Dispatchers.IO) {
                        downloadAllSongs(state, context)
                        Log.d("LibraryScreen", "Started at: ${LocalDateTime.now()}")
                        // }
                        // task.await()

                    }

                    musicFetchState = 1
                } else {
                    musicFetchState = 0
                }
                if (musicFetchState == 1) {
                    addRemoteSongsToDatabase(state, context, songDao)
                    if (state.isList.value) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .background(Black)
                                .padding(bottom = 68.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = lazyListState
                        ) {
                            itemsIndexed(state.remoteSongLibrary) { index, item ->
                                SongEntry(
                                    state = state,
                                    context = context,
                                    songDao = songDao,
                                    backStack = backStack,
                                    index = index,
                                    item = item,
                                    isLocal = false
                                )
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(bottom = 68.dp)
                                .background(Black),
                            columns = GridCells.Fixed(2),
                            state = lazyGridState,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(state.remoteSongLibrary) { index, item ->
                                SongEntryVertical(
                                    state = state,
                                    context = context,
                                    songDao = songDao,
                                    backStack = backStack,
                                    index = index,
                                    item = item,
                                    isLocal = false,
                                    dropdownItems = dropdownItems
                                )
                            }
                        }
                    }
                } else {
                    NoInternetConnectionScreen(state, viewModel)
                }
            }
        }
    } else {
        PermissionNotGrantedScreen(mediaPermission)
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionNotGrantedScreen(mediaPermission: PermissionState) {
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

@Composable
fun NoInternetConnectionScreen(state: LibraryMviState, viewModel: LibraryViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(Black)
            .padding(64.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.disconnected),
            contentDescription = "Disconnected",
            modifier = Modifier.size(80.dp),
            tint = Color.White
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Failed to retrieve songs. Please check your internet connection and try again",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = {
                state.hasAlreadyFetchedSongs.value = false
                if (!state.hasAlreadyFetchedSongs.value) {
                    viewModel.processIntent(LibraryMviIntents.GetMusicFromRemote)
                    state.hasAlreadyFetchedSongs.value = true
                }
            },
            modifier = Modifier.size(160.dp, 50.dp),

            ) {
            Text("Try again")
        }
    }
}

@Composable
fun LocalRemoteButtons(state: LibraryMviState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Black)
            .padding(top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { state.isLocal.value = true }, modifier = Modifier.size(120.dp, 40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isLocal.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                contentColor = if (state.isLocal.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Local")
        }
        Button(
            onClick = {
                state.isLocal.value = false
            }, modifier = Modifier.size(120.dp, 40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (!state.isLocal.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
                contentColor = if (!state.isLocal.value) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Remote")
        }
    }
}

@Composable
fun TopBar(
    viewModel: LibraryViewModel,
    state: LibraryMviState
) {
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
                painter = painterResource(id = if (state.isList.value) R.drawable.grid else R.drawable.hamburger_icon),
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
}

@Composable
//@Preview(showBackground = true)
fun SongEntry(
    state: LibraryMviState,
    context: Context,
    songDao: SongDao,
    backStack: SnapshotStateList<Screen>,
    index: Int,
    item: Song,
    isLocal: Boolean
) {
    val cover = if (isLocal) remember(state.songLibrary[index].songID) {
        convertBitmapToImage(state.songLibrary[index].cover, context)
    } else remember(state.remoteSongLibrary[index].songID) {
        convertBitmapToImage(state.remoteSongLibrary[index].cover, context)
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
                text = if (isLocal) state.songLibrary[index].title
                else state.remoteSongLibrary[index].title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = if (isLocal) state.songLibrary[index].artist
                else state.remoteSongLibrary[index].artist,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        }
        Text(
            text = millisToDuration(
                if (isLocal) state.songLibrary[index].duration
                else state.remoteSongLibrary[index].duration
            ),
            modifier = Modifier.weight(0.15f),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
            textAlign = TextAlign.End
        )
        var isDropdownMenuVisible by remember { mutableStateOf(false) }
        var dropdownItems = mutableListOf<DropdownItems>()
        dropdownItems.add(DropdownItems("Add to playlist", R.drawable.add_song))
        dropdownItems.add(DropdownItems("Remove from playlist", R.drawable.remove))
        dropdownItems.add(DropdownItems("Share", R.drawable.share))
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
                    SongDropdownItems(
                        state = state,
                        index = index,
                        item = item,
                        backStack = backStack,
                        songDao = songDao,
                        context = context,
                        onClick = { isDropdownMenuVisible = false },
                        dropdownItems = dropdownItems,
                        isLocal = isLocal
                    )
                }
            }
        }

    }
}


@Composable
fun SongDropdownItems(
    state: LibraryMviState,
    index: Int,
    item: Song,
    backStack: SnapshotStateList<Screen>,
    songDao: SongDao,
    context: Context,
    onClick: () -> Unit,
    dropdownItems: MutableList<DropdownItems>,
    isLocal: Boolean
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
            onClick
            IncomingSong.Song = if (isLocal)
                state.songLibrary[index] else state.remoteSongLibrary[index]
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
            onClick
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

@Composable
//@Preview(showBackground = false)
fun SongEntryVertical(
    state: LibraryMviState,
    index: Int,
    context: Context,
    dropdownItems: MutableList<DropdownItems>,
    backStack: SnapshotStateList<Screen>,
    songDao: SongDao,
    item: Song,
    isLocal: Boolean
) {
    val cover = if (isLocal) remember(state.songLibrary[index].songID) {
        convertBitmapToImage(state.songLibrary[index].cover, context)
    } else remember(state.remoteSongLibrary[index].songID) {
        convertBitmapToImage(state.remoteSongLibrary[index].cover, context)
    }
    val painter = if (cover != null) {
        rememberAsyncImagePainter(model = cover)
    } else {
        painterResource(R.drawable.cover_1)
    }
    var isDropdownMenuVisible by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
                            SongDropdownItems(
                                state = state,
                                index = index,
                                item = item,
                                backStack = backStack,
                                songDao = songDao,
                                context = context,
                                onClick = { isDropdownMenuVisible = false },
                                dropdownItems = dropdownItems,
                                isLocal = isLocal
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isLocal) state.songLibrary[index].title
            else state.remoteSongLibrary[index].title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isLocal) state.songLibrary[index].artist
            else state.remoteSongLibrary[index].artist,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            millisToDuration(
                if (isLocal) state.songLibrary[index].duration
                else state.remoteSongLibrary[index].duration
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )

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
    "${MediaStore.Audio.Media.TITLE} ASC"
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
                        songID = id.toInt(),
                        isLocal = 1
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

@Composable
fun AnimatedPreloader(modifier: Modifier = Modifier) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.lottie_remote_item_loading
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = { preloaderProgress },
        modifier = modifier
    )
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

private fun downloadSong(context: Context, fileName: String) {
    runBlocking {
        withContext(Dispatchers.IO) {
            val call = ApiClient.build().getSong(fileName)
            call.enqueue(object : Callback<ResponseBody> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when {
                        response.isSuccessful -> {
                            response.body()?.let { body ->
                                saveFileToInternalStorage(context, fileName, body)
                            }
                        }

                        response.code() == 400 -> Log.e("LibraryViewModel", "Bad Request")
                        response.code() == 401 -> Log.e("LibraryViewModel", "Unauthorized")
                        response.code() == 403 -> Log.e("LibraryViewModel", "Forbidden")
                        response.code() == 404 -> Log.e("LibraryViewModel", "Not Found")
                        response.code() == 500 -> Log.e("LibraryViewModel", "Internal Server Error")
                        else -> Log.e("LibraryViewModel", "Unknown Error")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("LibraryViewModel", "onFailure: ${t.message}")

                }
            })
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun saveFileToInternalStorage(context: Context, fileName: String, content: ResponseBody) {
    val directory = File(context.filesDir, "RemoteLibrary")
    if (!directory.exists()) {
        directory.mkdir()
    }
    val file = File(directory, fileName)
    if (!file.exists()) {
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val fileReader = ByteArray(4096)
            val fileSize = content.contentLength()
            var fileSizeDownloaded: Long = 0

            inputStream = content.byteStream()
            outputStream = FileOutputStream(file)

            val fileSizeFormat = DecimalFormat("#0.00")
            Log.d(
                "LibraryScreen",
                "$fileName file size: ${fileSizeFormat.format(fileSize.toDouble() / 1_048_576)}MB"
            )
            while (true) {
                val read = inputStream.read(fileReader)
                if (read == -1) {
                    break
                }
                outputStream.write(fileReader, 0, read)
                fileSizeDownloaded += read.toLong()

            }

            outputStream.flush()
            Log.d("LibraryScreen", "Finished downloading $fileName at ${LocalDateTime.now()}")
        } catch (e: IOException) {
            Log.e("LibraryScreen", e.message.toString())
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

}


private fun getInternalStorageFilePath(context: Context, fileName: String): String {
    val directory = File(context.filesDir, "RemoteLibrary")
    val file = File(directory, fileName)
    return if (file.exists()) {
        file.path
    } else "not_found"
}

private fun addRemoteSongsToDatabase(
    state: LibraryMviState,
    context: Context,
    songDao: SongDao
) {
    for (i in 0..state.remoteSongLibrary.size - 1) {
        state.remoteSongLibrary[i].path = getInternalStorageFilePath(
            context,
            state.remoteSongLibrary[0].path.substringAfterLast('/')
        )
        runBlocking {
            withContext(Dispatchers.IO) {
                if (songDao.getSong(state.remoteSongLibrary[i].title).isEmpty()) {
                    songDao.insert(state.remoteSongLibrary[i])
                } else {
                    songDao.updateSongPath(
                        state.remoteSongLibrary[i].title,
                        state.remoteSongLibrary[i].path
                    )
                }
            }
        }
    }
}

private suspend fun downloadAllSongs(
    state: LibraryMviState,
    context: Context
) {
    for (i in 0..state.remoteSongLibrary.size - 1) {
        downloadSong(
            context,
            state.remoteSongLibrary[i].path.substringAfterLast('/')
        )
    }
}

