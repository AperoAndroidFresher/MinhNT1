package com.apero.minhnt1.screens.playlist

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apero.minhnt1.DropdownItems
import com.apero.minhnt1.R
import com.apero.minhnt1.Song
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
//@Preview(showBackground = true)
fun PlaylistScreen(viewModel: PlaylistViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    state.playlist = addSongs(state.playlist)

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
                painter = painterResource(id = if (state.isList.value) R.drawable.grid else R.drawable.hamburger_icon),
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
            itemsIndexed(state.playlist) { index, item ->
//                SongEntry(
//                    cover = playlist[index].cover,
//                    title = playlist[index].title,
//                    author = playlist[index].author,
//                    length = playlist[index].length
//                )

                Row(
                    modifier = Modifier
                        .background(Black)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = item.cover),
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
                            item.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            item.author,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Text(
                        item.length,
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
                                        state.playlist.removeAt(index)
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
            items(state.playlist.size) { index ->
//                SongEntryVertical(
//                    cover = playlist[index].cover,
//                    title = playlist[index].title,
//                    author = playlist[index].author,
//                    length = playlist[index].length
//                )
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
                            painter = painterResource(id = state.playlist[index].cover),
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
                                                state.playlist.removeAt(index)
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
                        state.playlist[index].title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        state.playlist[index].author,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        state.playlist[index].length,
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

fun addSongs(playlist: SnapshotStateList<Song>): SnapshotStateList<Song> {
    playlist.add(
        Song(
            cover = R.drawable.cover_1,
            title = "GOODNIGHT",
            author = "STOMACH BOOK",
            length = "03:17"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_2,
            title = "Boys For Pele",
            author = "Tori Amos",
            length = "04:26"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_3,
            title = "metallic life review",
            author = "matmos",
            length = "02:59"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_4,
            title = "More",
            author = "Pulp",
            length = "03:32"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_1,
            title = "STATIC CHAOS",
            author = "STOMACH BOOK",
            length = "02:46"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_2,
            title = "Return to Form",
            author = "Tori Amos",
            length = "03:20"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_3,
            title = "illusion",
            author = "matmos",
            length = "04:01"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_4,
            title = "Endurance",
            author = "Pulp",
            length = "02:59"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_1,
            title = "RADIANCE",
            author = "STOMACH BOOK",
            length = "03:50"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_2,
            title = "Compassionate",
            author = "Tori Amos",
            length = "02:39"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_3,
            title = "foggy morning",
            author = "matmos",
            length = "04:01"
        )
    )
    playlist.add(
        Song(
            cover = R.drawable.cover_4,
            title = "Mission",
            author = "Pulp",
            length = "02:59"
        )
    )
    return playlist
}