package com.apero.minhnt1.screens.playlist

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.minhnt1.DropdownItems
import com.apero.minhnt1.R
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.BitmapImage
import coil3.asImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
//@Preview(showBackground = true)
fun PlaylistScreen(context: Context, viewModel: PlaylistViewModel = viewModel()) {
    var resolver = context.contentResolver
    val state by viewModel.state.collectAsStateWithLifecycle()
    var isList by remember { mutableStateOf(false) }

    var playlist = remember { mutableStateListOf<Song>() }
    playlist.clear()
    playlist = populatePlaylist(resolver, playlist)
    var imageData by remember { mutableStateOf<Bitmap?>(null) }

    var cover = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current)
            .data(imageData)
            .crossfade(true)
            .size(300, 300)
            .build()
    )
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    val dropdownItems = remember { mutableStateListOf<DropdownItems>() }
    dropdownItems.add(DropdownItems("Remove from playlist", R.drawable.remove))
    dropdownItems.add(DropdownItems("Share (coming soon)", R.drawable.share))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Black)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "My Playlist",
            modifier = Modifier
                .weight(0.8f)
                .padding(start = 96.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        IconButton(onClick = {
            viewModel.processIntent(PlaylistMviIntents.SwitchView)
        }, enabled = true) {
            Icon(
                painter = painterResource(id = if (isList) R.drawable.grid else R.drawable.hamburger_icon),
                contentDescription = "Grid",
                modifier = Modifier.weight(0.1f),
                tint = Color.White
            )
        }
        IconButton(onClick = {

        }, enabled = true) {
            Icon(
                painter = painterResource(id = R.drawable.sort_descending),
                contentDescription = "Sort",
                modifier = Modifier.weight(0.1f),
                tint = Color.White
            )
        }
    }
    if (state.isList.value) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp)
                .navigationBarsPadding()
                .background(Black),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            var counter = 0
            items(playlist.size) { index ->
//                SongEntry(
//                    cover = playlist[index].cover,
//                    title = playlist[index].title,
//                    author = playlist[index].author,
//                    length = playlist[index].length
//                )
                counter++
                Log.d("ListItem Count", counter.toString())

                Row(
                    modifier = Modifier
                        .background(Black)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = cover,
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
                            playlist[index].title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            playlist[index].artist,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Text(
                        playlist[index].duration.toString(),
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
                                    text = { Text(dropdownItems[0].text, color = Color.White) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = dropdownItems[0].icon),
                                            contentDescription = "Remove",
                                            tint = Color.White
                                        )
                                    },
                                    onClick = {
                                        playlist.removeAt(index)
                                        isDropdownMenuVisible = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(dropdownItems[1].text, color = Color.Gray) },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = dropdownItems[1].icon),
                                            contentDescription = "Share",
                                            tint = Color.White
                                        )
                                    },
                                    onClick = {
                                        isDropdownMenuVisible = false
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
                .padding(top = 80.dp)
                .background(Black),
            columns = GridCells.Fixed(2),
            state = lazyGridState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            var counter = 0
            items(playlist.size) { index ->
//                SongEntryVertical(
//                    cover = playlist[index].cover,
//                    title = playlist[index].title,
//                    author = playlist[index].author,
//                    length = playlist[index].length
//                )
                counter++
                Log.d("LVG Count", counter.toString())
                imageData = convertBitmapToImage(playlist[index].cover, context = context)

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
                            painter = cover,
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
                                                    contentDescription = "Remove",
                                                    tint = Color.White
                                                )
                                            },
                                            onClick = {
                                                playlist.removeAt(index)
                                                isDropdownMenuVisible = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dropdownItems[1].text,
                                                    color = Color.Gray
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
                                                isDropdownMenuVisible = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        playlist[index].title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        playlist[index].artist,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        playlist[index].duration.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                }
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

private fun populatePlaylist(
    resolver: ContentResolver?,
    playlist: SnapshotStateList<Song>
): SnapshotStateList<Song> {
    var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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

        //val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        when {
            !cursor.moveToFirst() -> {
                Log.e("PlaylistScreen", "No media found")
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

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        it.getString(idColumn).toLong()
                    )
                    //var data = retriever.embeddedPicture
                    //val cover = BitmapFactory.decodeByteArray(data, 0, data!!.size).asImage()
                    playlist.add(
                        Song(
                            title = title,
                            artist = artist,
                            duration = duration,
                            path = path,
                            cover = contentUri
                        )
                    )
                } while (cursor.moveToNext())
            }
        }

    }
    return playlist

}

fun convertBitmapToImage(contentUri: Uri, context: Context): Bitmap? {
    var retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(context, contentUri)
        var data = retriever.embeddedPicture
        if (data != null) return BitmapFactory.decodeByteArray(data, 0, data.size)
    } catch (e: Exception) {

    } finally {
        retriever.release()
    }
    return null
}

data class Song(
    val cover: Uri,
    val title: String,
    val artist: String,
    val duration: Long,
    val path: Any
)