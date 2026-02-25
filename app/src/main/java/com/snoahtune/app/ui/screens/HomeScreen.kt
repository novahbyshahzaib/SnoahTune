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
import com.snoahtune.app.domain.model.Song
import com.snoahtune.app.ui.components.*
import com.snoahtune.app.ui.theme.*
import com.snoahtune.app.viewmodel.HomeViewModel
import com.snoahtune.app.viewmodel.PlayerViewModel
import com.snoahtune.app.viewmodel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeVM: HomeViewModel,
    playerVM: PlayerViewModel
) {
    val songs     by homeVM.filteredSongs.collectAsState()
    val query     by homeVM.searchQuery.collectAsState()
    val loading   by homeVM.isLoading.collectAsState()
    val currentSong by playerVM.currentSong.collectAsState()
    val isPlaying   by playerVM.isPlaying.collectAsState()

    var showSortSheet  by remember { mutableStateOf(false) }
    var showOptionsFor by remember { mutableStateOf<Song?>(null) }

    Column(Modifier.fillMaxSize().background(Background)) {

        // ── Top Bar ─────────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .background(ElectricYellow)
                .border(BorderStroke(2.dp, BorderBlack))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SNOAHTUNE",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp
                )
            )
            IconButton(onClick = { homeVM.refreshSongs() }) {
                Icon(Icons.Default.Refresh, "Refresh", tint = BorderBlack)
            }
        }

        // ── Search Bar ──────────────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, BorderBlack)
                .background(SurfaceWhite)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, null, tint = TextSecondary)
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = query,
                onValueChange = homeVM::setSearchQuery,
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { inner ->
                    if (query.isEmpty()) Text("Search songs, artists, albums…", color = TextSecondary)
                    inner()
                }
            )
            if (query.isNotEmpty()) {
                IconButton(onClick = { homeVM.setSearchQuery("") }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, null, tint = TextSecondary)
                }
            }
        }

        // ── Sort Row ────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${songs.size} SONGS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                letterSpacing = 1.sp
            )
            TextButton(onClick = { showSortSheet = true }) {
                Icon(Icons.Default.Sort, null, tint = ElectricBlue)
                Spacer(Modifier.width(4.dp))
                Text("SORT", color = ElectricBlue, fontWeight = FontWeight.ExtraBold,
                     style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp))
            }
        }

        // ── Song List ───────────────────────────────────────────
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ElectricBlue)
            }
        } else if (songs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("NO SONGS FOUND", fontWeight = FontWeight.ExtraBold, color = TextSecondary,
                     letterSpacing = 2.sp)
            }
        } else {
            LazyColumn(Modifier.fillMaxSize()) {
                items(songs, key = { it.id }) { song ->
                    SongItem(
                        song      = song,
                        isPlaying = currentSong?.id == song.id && isPlaying,
                        onClick   = { playerVM.playSong(song, songs) },
                        onMoreClick = { showOptionsFor = song }
                    )
                }
                item { Spacer(Modifier.height(140.dp)) }
            }
        }
    }

    // ── Sort Bottom Sheet ────────────────────────────────────────
    if (showSortSheet) {
        ModalBottomSheet(onDismissRequest = { showSortSheet = false },
            containerColor = Background) {
            Column(Modifier.padding(16.dp)) {
                Text("SORT BY", fontWeight = FontWeight.ExtraBold,
                     letterSpacing = 2.sp, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                val opts = listOf(
                    "Date Added (Newest)" to SortOrder.DATE_ADDED_DESC,
                    "Date Added (Oldest)" to SortOrder.DATE_ADDED_ASC,
                    "Name A–Z"            to SortOrder.NAME_ASC,
                    "Name Z–A"            to SortOrder.NAME_DESC,
                    "Duration (Longest)"  to SortOrder.DURATION_DESC,
                    "Duration (Shortest)" to SortOrder.DURATION_ASC,
                    "Artist Name"         to SortOrder.ARTIST
                )
                opts.forEach { (label, order) ->
                    TextButton(onClick = { homeVM.setSortOrder(order); showSortSheet = false },
                        modifier = Modifier.fillMaxWidth()) {
                        Text(label.uppercase(), fontWeight = FontWeight.Bold,
                             letterSpacing = 1.sp, color = BorderBlack)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // ── Song Options Sheet ───────────────────────────────────────
    showOptionsFor?.let { song ->
        ModalBottomSheet(onDismissRequest = { showOptionsFor = null },
            containerColor = Background) {
            Column(Modifier.padding(16.dp)) {
                Text(song.title, fontWeight = FontWeight.ExtraBold,
                     style = MaterialTheme.typography.titleMedium)
                Text(song.artist, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.height(16.dp))
                listOf(
                    Icons.Default.PlayArrow to "Play",
                    Icons.Default.Favorite to "Add to Favorites",
                    Icons.Default.PlaylistAdd to "Add to Playlist"
                ).forEach { (icon, label) ->
                    TextButton(
                        onClick = {
                            when (label) {
                                "Play" -> playerVM.playSong(song, songs)
                                "Add to Favorites" -> {} // handled via now playing
                            }
                            showOptionsFor = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(icon, null, tint = BorderBlack)
                        Spacer(Modifier.width(12.dp))
                        Text(label.uppercase(), fontWeight = FontWeight.Bold,
                             letterSpacing = 1.sp, color = BorderBlack)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
