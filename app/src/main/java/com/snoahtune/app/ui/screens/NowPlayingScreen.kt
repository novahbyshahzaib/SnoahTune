package com.snoahtune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.media3.common.Player
import coil.compose.AsyncImage
import com.snoahtune.app.ui.theme.*
import com.snoahtune.app.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playerVM: PlayerViewModel,
    onBack: () -> Unit
) {
    val song       by playerVM.currentSong.collectAsState()
    val isPlaying  by playerVM.isPlaying.collectAsState()
    val progress   by playerVM.progress.collectAsState()
    val position   by playerVM.currentPosition.collectAsState()
    val isFavorite by playerVM.isFavorite.collectAsState()
    val shuffle    by playerVM.shuffleEnabled.collectAsState()
    val repeatMode by playerVM.repeatMode.collectAsState()
    val speed      by playerVM.playbackSpeed.collectAsState()

    var showSpeedSheet by remember { mutableStateOf(false) }
    var dragX by remember { mutableFloatStateOf(0f) }

    if (song == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "NO SONG PLAYING",
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = TextSecondary
            )
        }
        return
    }

    val s = song!!

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Top Bar ──────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Back",
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                "NOW PLAYING",
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                style = MaterialTheme.typography.labelSmall
            )
            IconButton(onClick = {}) {
                Icon(Icons.Default.MoreVert, contentDescription = null)
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Album Art ─────────────────────────────────────────────
        Box(
            Modifier
                .size(300.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragX < -80f) playerVM.skipNext()
                            else if (dragX > 80f) playerVM.skipPrevious()
                            dragX = 0f
                        }
                    ) { _, delta -> dragX += delta }
                },
            contentAlignment = Alignment.Center
        ) {
            // Shadow block
            Box(
                Modifier
                    .size(260.dp)
                    .offset(x = 6.dp, y = 6.dp)
                    .rotate(-2f)
                    .background(BorderBlack)
            )
            // Art box
            Box(
                Modifier
                    .size(260.dp)
                    .rotate(-2f)
                    .border(3.dp, BorderBlack)
                    .background(ElectricYellow)
            ) {
                AsyncImage(
                    model = "content://media/external/audio/albumart/${s.albumId}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // ── Song Info ─────────────────────────────────────────────
        Text(
            s.title,
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(4.dp))
        Text(
            s.artist,
            style = MaterialTheme.typography.titleMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Text(
            s.album,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // ── Action Row ────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { playerVM.toggleFavorite() }) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) HotPink else BorderBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
            TextButton(onClick = { showSpeedSheet = true }) {
                Text(
                    "${speed}x",
                    fontWeight = FontWeight.ExtraBold,
                    color = ElectricBlue,
                    fontSize = 16.sp
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    Icons.Default.Equalizer,
                    contentDescription = "EQ",
                    tint = BorderBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // ── Seek Bar ──────────────────────────────────────────────
        Column(Modifier.fillMaxWidth()) {
            Slider(
                value = progress.coerceIn(0f, 1f),
                onValueChange = { playerVM.seekTo(it) },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = BorderBlack,
                    activeTrackColor = BorderBlack,
                    inactiveTrackColor = BorderBlack.copy(alpha = 0.2f)
                )
            )
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    PlayerViewModel.msToString(position),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Text(
                    PlayerViewModel.msToString(s.duration),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Playback Controls ─────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(onClick = { playerVM.toggleShuffle() }) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (shuffle) HotPink else TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Previous
            Box(
                Modifier
                    .size(52.dp)
                    .background(BorderBlack),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { playerVM.skipPrevious() }) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Play / Pause
            Box(
                Modifier
                    .size(72.dp)
                    .background(ElectricYellow)
                    .border(3.dp, BorderBlack)
                    .clickable { playerVM.playPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = BorderBlack,
                    modifier = Modifier.size(44.dp)
                )
            }

            // Next
            Box(
                Modifier
                    .size(52.dp)
                    .background(BorderBlack),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { playerVM.skipNext() }) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = null,
                        tint = ElectricYellow,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Repeat
            IconButton(onClick = { playerVM.toggleRepeat() }) {
                Icon(
                    when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) HotPink else TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }

    // ── Speed Bottom Sheet ────────────────────────────────────────
    if (showSpeedSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSpeedSheet = false },
            containerColor = Background
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "PLAYBACK SPEED",
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f).forEach { spd ->
                    TextButton(
                        onClick = {
                            playerVM.setPlaybackSpeed(spd)
                            showSpeedSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "${spd}x",
                            fontWeight = if (spd == speed) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (spd == speed) ElectricBlue else BorderBlack,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
