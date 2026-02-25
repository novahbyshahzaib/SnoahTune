package com.snoahtune.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.snoahtune.app.ui.components.NeuButton
import com.snoahtune.app.ui.components.NeuCard
import com.snoahtune.app.ui.components.SongItem
import com.snoahtune.app.ui.theme.*
import com.snoahtune.app.viewmodel.HomeViewModel
import com.snoahtune.app.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(homeVM: HomeViewModel, playerVM: PlayerViewModel) {
    val tabs      = listOf("FAVORITES", "ALBUMS", "PLAYLISTS")
    var selectedTab by remember { mutableIntStateOf(0) }
    val favSongs  by homeVM.favoriteSongs.collectAsState()
    val albums    by homeVM.albums.collectAsState()
    val songs     by homeVM.filteredSongs.collectAsState()
    val playlists by homeVM.playlists.collectAsState()
    val isPlaying by playerVM.isPlaying.collectAsState()
    val currSong  by playerVM.currentSong.collectAsState()

    var newPlaylistName by remember { mutableStateOf("") }
    var showNewPlaylist by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Background)) {
        // Header
        Box(
            Modifier.fillMaxWidth().background(HotPink)
                .border(BorderStroke(2.dp, BorderBlack)).padding(16.dp)
        ) {
            Text("LIBRARY", style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp), color = SurfaceWhite)
        }

        // Tabs
        Row(Modifier.fillMaxWidth().background(SurfaceWhite)
            .border(BorderStroke(2.dp, BorderBlack))) {
            tabs.forEachIndexed { i, tab ->
                Box(
                    Modifier
                        .weight(1f)
                        .background(if (i == selectedTab) ElectricYellow else SurfaceWhite)
                        .border(BorderStroke(1.dp, BorderBlack))
                        .clickable { selectedTab = i }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(tab, fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp))
                }
            }
        }

        when (selectedTab) {
            // ── Favorites ───────────────────────────────────────
            0 -> {
                if (favSongs.isEmpty()) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FavoriteBorder, null,
                                modifier = Modifier.size(64.dp), tint = TextSecondary)
                            Spacer(Modifier.height(12.dp))
                            Text("NO FAVORITES YET", fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 2.sp, color = TextSecondary)
                            Text("Tap ♥ on any song", color = TextSecondary,
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(favSongs, key = { it.id }) { song ->
                            SongItem(
                                song = song,
                                isPlaying = currSong?.id == song.id && isPlaying,
                                onClick = { playerVM.playSong(song, favSongs) },
                                onMoreClick = {}
                            )
                        }
                        item { Spacer(Modifier.height(140.dp)) }
                    }
                }
            }

            // ── Albums ──────────────────────────────────────────
            1 -> {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(albums, key = { it.id }) { album ->
                        NeuCard(
                            modifier = Modifier.fillMaxWidth().padding(12.dp, 4.dp),
                            onClick = {
                                val albumSongs = songs.filter { it.albumId == album.id }
                                if (albumSongs.isNotEmpty())
                                    playerVM.playSong(albumSongs.first(), albumSongs)
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier.size(56.dp).background(ElectricBlue)
                                        .border(2.dp, BorderBlack),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Album, null,
                                        tint = SurfaceWhite, modifier = Modifier.size(32.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(album.name, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                                    Text("${album.artist} · ${album.songCount} songs",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary)
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(140.dp)) }
                }
            }

            // ── Playlists ────────────────────────────────────────
            2 -> {
                Column(Modifier.fillMaxSize()) {
                    NeuButton(
                        text = "+ NEW PLAYLIST",
                        onClick = { showNewPlaylist = true },
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        backgroundColor = LimeGreen
                    )
                    if (playlists.isEmpty()) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PlaylistPlay, null,
                                    modifier = Modifier.size(64.dp), tint = TextSecondary)
                                Spacer(Modifier.height(12.dp))
                                Text("NO PLAYLISTS YET", fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp, color = TextSecondary)
                                Text("Tap + NEW PLAYLIST to create one",
                                    color = TextSecondary,
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    } else {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(playlists, key = { it.id }) { playlist ->
                                NeuCard(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp, 4.dp),
                                    onClick = {}
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            Modifier.size(56.dp).background(HotPink)
                                                .border(2.dp, BorderBlack),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.PlaylistPlay, null,
                                                tint = SurfaceWhite, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(Modifier.weight(1f)) {
                                            Text(playlist.name, fontWeight = FontWeight.ExtraBold)
                                            Text("Playlist", style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary)
                                        }
                                        IconButton(onClick = { homeVM.deletePlaylist(playlist) }) {
                                            Icon(Icons.Default.Delete, null, tint = TextSecondary)
                                        }
                                    }
                                }
                            }
                            item { Spacer(Modifier.height(140.dp)) }
                        }
                    }
                }
            }
        }
    }

    // ── New Playlist Dialog ──────────────────────────────────────
    if (showNewPlaylist) {
        AlertDialog(
            onDismissRequest = { showNewPlaylist = false; newPlaylistName = "" },
            containerColor = SurfaceWhite,
            title = {
                Text("NEW PLAYLIST", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
            },
            text = {
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text("Playlist name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newPlaylistName.isNotBlank()) {
                        homeVM.createPlaylist(newPlaylistName)  // ✅ Actually calls the function now!
                        newPlaylistName = ""
                        showNewPlaylist = false
                    }
                }) {
                    Text("CREATE", fontWeight = FontWeight.ExtraBold, color = ElectricBlue)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewPlaylist = false; newPlaylistName = "" }) {
                    Text("CANCEL", color = TextSecondary)
                }
            }
        )
    }
}
