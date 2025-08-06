package com.apero.minhnt1.screens.playlist

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.apero.minhnt1.DropdownItems
import com.apero.minhnt1.R
import com.apero.minhnt1.database.AppDatabase
import com.apero.minhnt1.database.playlist.Playlist
import com.apero.minhnt1.database.playlist.PlaylistDao
import com.apero.minhnt1.screens.library.IncomingSong
import com.apero.minhnt1.utility.convertBitmapToImage
import com.apero.minhnt1.utility.millisToDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


@Composable
fun PlaylistScreen(
    context: Context,
    viewModel: PlaylistViewModel = viewModel(),
    isAlreadyLaunched: Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showAddToPlaylistPopup by remember { mutableStateOf(IncomingSong.showAddToPlaylistPopup) }
    var showPlaylistContents by remember { mutableStateOf(false) }
    var playlistIndex by remember { mutableIntStateOf(0) }
    var showCreatePlaylist by remember { mutableStateOf(false) }
    val playlistDao = AppDatabase.getDatabase(context).playlistDao()
    val dropdownItems = remember { mutableStateListOf<DropdownItems>() }
    dropdownItems.add(DropdownItems("Remove playlist", R.drawable.remove))
    dropdownItems.add(DropdownItems("Rename", R.drawable.pencil))

    state.playlistLibrary = populatePlaylist(playlistDao)

    val lazyListState = rememberLazyListState()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(80.dp)
                .background(Black)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "My Playlist",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (state.playlistLibrary.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .background(Black)
                    .padding(64.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You currently don't have any playlists. Click the + button below to create a new one.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        showCreatePlaylist = true
                    },
                    modifier = Modifier.size(120.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "Grid",

                        tint = Color.White
                    )
                }
            }
        } else if (showPlaylistContents) {
            PlaylistContentScreen(context, state, playlistIndex, playlistDao)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .background(Black),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = lazyListState
            ) {
                itemsIndexed(state.playlistLibrary) { index, item ->
                    val cover = if (state.playlistLibrary[index].songList.isNotEmpty()) remember(
                        state.playlistLibrary[index].songList[0]?.songID
                    ) {
                        convertBitmapToImage(
                            state.playlistLibrary[index].songList[0]?.cover,
                            context
                        )
                    } else null

                    val painter = if (cover != null) {
                        rememberAsyncImagePainter(model = cover)
                    } else {
                        painterResource(R.drawable.cover_1)
                    }

                    Row(
                        modifier = Modifier
                            .background(Black)
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                playlistIndex = index
                                showPlaylistContents = true
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = "Playlist cover",
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.CenterStart,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(10))
                        )
                        Column(
                            modifier = Modifier.weight(0.7f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${item.songList.size} songs",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
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
                                            Text(dropdownItems[0].text, color = Color.White)
                                        },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = dropdownItems[0].icon),
                                                contentDescription = "Remove",
                                                tint = Color.White
                                            )
                                        },
                                        onClick = {

                                            isDropdownMenuVisible = false
                                            runBlocking {
                                                withContext(Dispatchers.IO) {
                                                    playlistDao.delete(state.playlistLibrary[index])
                                                }
                                            }
                                            state.playlistLibrary.removeAt(index)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(dropdownItems[1].text, color = Color.White) },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = dropdownItems[1].icon),
                                                contentDescription = "Rename",
                                                tint = Color.White
                                            )
                                        },
                                        onClick = {
                                            state.showRenameDialog.value = true
                                            isDropdownMenuVisible = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    if (state.showRenameDialog.value) RenameDialog(state, index)
                }
            }

        }
        if (showCreatePlaylist) {
            //CreatePlaylistDialog(onDismissRequest = {showCreatePlaylist = false}, state)
            Dialog(onDismissRequest = { showCreatePlaylist = false }) {
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
                        var text by remember { mutableStateOf("") }
                        Text(
                            text = "New Playlist",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color(0xff2BB673)
                        )
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            shape = RoundedCornerShape(25),
                            placeholder = { Text("Give your playlist a title") },
                            singleLine = true,

                            )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Black),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TextButton(onClick = {}) {
                                Text("Cancel")
                            }
                            TextButton(onClick = {
                                runBlocking {
                                    withContext(Dispatchers.IO) {
                                        playlistDao.insert(Playlist(name = text))
                                    }
                                }

                                state.playlistLibrary.add(Playlist(name = text))
                                showCreatePlaylist = false
                            }) {
                                Text("Create", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }

        if (showAddToPlaylistPopup && state.playlistLibrary.isNotEmpty()) {
            Dialog(onDismissRequest = {
                IncomingSong.showAddToPlaylistPopup = false
                showAddToPlaylistPopup = false
            }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        Text(
                            text = "Choose Playlist",
                            modifier = Modifier
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color.White
                        )
                        Spacer(Modifier.height(10.dp))
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Black),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = lazyListState
                        ) {
                            itemsIndexed(state.playlistLibrary) { index, item ->
                                Row(
                                    modifier = Modifier
                                        .background(Black)
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            IncomingSong.Song.inPlaylistID =
                                                state.playlistLibrary[index].playlistID
                                            state.playlistLibrary[index].songList.add(IncomingSong.Song)
                                            showAddToPlaylistPopup = false
                                            IncomingSong.showAddToPlaylistPopup = false
                                            runBlocking {
                                                withContext(Dispatchers.IO) {
                                                    playlistDao.update(state.playlistLibrary[index])
                                                }
                                            }
                                        },
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(state.playlistLibrary[index].playlistCover),
                                        contentDescription = "Playlist cover",
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.CenterStart,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(10))
                                    )
                                    Column(
                                        modifier = Modifier.weight(0.7f),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            item.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${item.songList.size} songs",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Gray
                                        )
                                    }

                                }
                                if (state.showRenameDialog.value) RenameDialog(state, index)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CreatePlaylistDialog(
        onDismissRequest: () -> Unit,
        state: PlaylistMviState
    ) {
    }
}

@Composable
fun PlaylistContentScreen(
    context: Context,
    state: PlaylistMviState,
    playlistIndex: Int,
    playlistDao: PlaylistDao
) {
    var playlistNotEmpty by remember { mutableStateOf(true) }
    var playlistContent = state.playlistLibrary[playlistIndex].songList
    val dropdownItems = remember { mutableStateListOf<DropdownItems>() }
    dropdownItems.add(DropdownItems("Remove from playlist", R.drawable.remove))
    dropdownItems.add(DropdownItems("Share", R.drawable.share))
    if (playlistNotEmpty) {
        val lazyListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(Black),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            items(playlistContent.size) { index ->

                val cover = remember(state.playlistLibrary[playlistIndex].songList[index]?.songID) {
                    convertBitmapToImage(
                        state.playlistLibrary[playlistIndex].songList[index]?.cover,
                        context
                    )
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
                            state.playlistLibrary[playlistIndex].songList[index]?.title!!,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            state.playlistLibrary[playlistIndex].songList[index]?.artist!!,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Text(
                        millisToDuration(
                            state.playlistLibrary[playlistIndex].songList[index]?.duration!!
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
                                            contentDescription = "Remove from playlist",
                                            tint = Color.White
                                        )
                                    },
                                    onClick = {
                                        state.playlistLibrary[playlistIndex].songList.removeAt(index)
                                        playlistNotEmpty =
                                            state.playlistLibrary[playlistIndex].songList.isNotEmpty()
                                        isDropdownMenuVisible = false
                                        runBlocking {
                                            withContext(Dispatchers.IO) {
                                                playlistDao.update(state.playlistLibrary[playlistIndex])
                                            }
                                        }
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text(dropdownItems[1].text, color = Color.White) },
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
                                                state.playlistLibrary[playlistIndex].songList[index]?.cover
                                            )
                                            type = "audio/*"
                                        }
                                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                        val shareIntent = Intent.createChooser(sendIntent, null)
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
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Playlist is empty",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }
    }
}

private fun populatePlaylist(playlistDao: PlaylistDao): SnapshotStateList<Playlist> {
    var listOfPlaylists: List<Playlist>
    val stateList = mutableStateListOf<Playlist>()
    runBlocking {
        withContext(Dispatchers.IO) {
            listOfPlaylists = playlistDao.getAll()
        }
    }
    for (playlist in listOfPlaylists) stateList.add(playlist)
    return stateList
}


@Composable
fun RenameDialog(
    state: PlaylistMviState,
    index: Int
) {
    Dialog(onDismissRequest = { state.showRenameDialog.value = false }) {
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
                var text by remember { mutableStateOf("") }
                Text(
                    text = "Rename",
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color(0xff2BB673)
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    shape = RoundedCornerShape(25),
                    placeholder = { Text("Give your playlist a new name") },
                    singleLine = true,

                    )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Black),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = { state.showRenameDialog.value = false }) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        state.playlistLibrary[index].name = text
                        state.showRenameDialog.value = false
                    }) {
                        Text("Rename", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
