package com.snoahtune.app.ui.screens

import android.content.Intent
import android.media.audiofx.AudioEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.media3.common.Player
import coil.compose.SubcomposeAsyncImage
import com.snoahtune.app.ui.theme.*
import com.snoahtune.app.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playerVM: PlayerViewModel,
    onBack: () -> Unit
) {
    val context       = LocalContext.current
    val song          by playerVM.currentSong.collectAsState()
    val isPlaying     by playerVM.isPlaying.collectAsState()
    val progress      by playerVM.progress.collectAsState()
    val position      by playerVM.currentPosition.collectAsState()
    val isFavorite    by playerVM.isFavorite.collectAsState()
    val shuffle       by playerVM.shuffleEnabled.collectAsState()
    val repeatMode    by playerVM.repeatMode.collectAsState()
    val speed         by playerVM.playbackSpeed.collectAsState()
    val slowedReverb  by playerVM.slowedReverbOn.collectAsState()

    var showSpeedSheet   by remember { mutableStateOf(false) }
    var showMoreMenu     by remember { mutableStateOf(false) }
    var dragX            by remember { mutableFloatStateOf(0f) }

    if (song == null) {
        Box(Modifier.fillMaxSize().background(Background), Alignment.Center) {
            Text("NO SONG PLAYING", fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp, color = TextSecondary)
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
                Icon(Icons.Default.KeyboardArrowDown, "Back", modifier = Modifier.size(32.dp))
            }
            Text("NOW PLAYING", fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp, style = MaterialTheme.typography.labelSmall)

            // 3-DOT MENU — now actually works!
            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(Icons.Default.MoreVert, null)
                }
                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add to Favorites", fontWeight = FontWeight.Bold) },
                        onClick = { playerVM.toggleFavorite(); showMoreMenu = false },
                        leadingIcon = { Icon(Icons.Default.Favorite, null, tint = HotPink) }
                    )
                    DropdownMenuItem(
                        text = { Text("Share Song", fontWeight = FontWeight.Bold) },
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT,
                                    "Listening to: ${s.title} by ${s.artist} 🎵")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share"))
                            showMoreMenu = false
                        },
                        leadingIcon = { Icon(Icons.Default.Share, null, tint = ElectricBlue) }
                    )
                    DropdownMenuItem(
                        text = { Text("Song Info", fontWeight = FontWeight.Bold) },
                        onClick = { showMoreMenu = false },
                        leadingIcon = { Icon(Icons.Default.Info, null) }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

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
            Box(Modifier.size(260.dp).offset(x = 6.dp, y = 6.dp).rotate(-2f).background(BorderBlack))
            Box(
                Modifier.size(260.dp).rotate(-2f).border(3.dp, BorderBlack).background(ElectricYellow),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = "content://media/external/audio/albumart/${s.albumId}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = {
                        Box(Modifier.fillMaxSize().background(ElectricYellow),
                            contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.MusicNote, null,
                                tint = BorderBlack, modifier = Modifier.size(80.dp))
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Song Info ─────────────────────────────────────────────
        Text(s.title, style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))
        Text(s.artist, style = MaterialTheme.typography.titleMedium,
            color = TextSecondary, textAlign = TextAlign.Center)
        Text(s.album, style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary, textAlign = TextAlign.Center)

        Spacer(Modifier.height(20.dp))

        // ── Action Row ────────────────────────────────────────────
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favorite
            IconButton(onClick = { playerVM.toggleFavorite() }) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    "Favorite",
                    tint = if (isFavorite) HotPink else BorderBlack,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Speed button
            TextButton(onClick = { showSpeedSheet = true }) {
                Text("${speed}x", fontWeight = FontWeight.ExtraBold,
                    color = ElectricBlue, fontSize = 16.sp)
            }

            // EQ button — now actually opens system equalizer!
            IconButton(onClick = {
                try {
                    val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL).apply {
                        putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.packageName)
                        putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // EQ not available on this device — do nothing
                }
            }) {
                Icon(Icons.Default.Equalizer, "Equalizer", tint = BorderBlack,
                    modifier = Modifier.size(28.dp))
            }
        }

        // ── Slowed + Reverb Toggle ────────────────────────────────
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .background(if (slowedReverb) HotPink else SurfaceWhite)
                .border(2.dp, BorderBlack)
                .clickable { playerVM.toggleSlowedReverb() }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "🌙 SLOWED + REVERB",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp,
                    color = if (slowedReverb) SurfaceWhite else BorderBlack
                )
                Text(
                    if (slowedReverb) "ON — pitch follows speed" else "OFF — tap to enable",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (slowedReverb) SurfaceWhite.copy(alpha = 0.8f) else TextSecondary
                )
            }
            Switch(
                checked = slowedReverb,
                onCheckedChange = { playerVM.toggleSlowedReverb() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceWhite,
                    checkedTrackColor = BorderBlack,
                    uncheckedThumbColor = BorderBlack,
                    uncheckedTrackColor = TextSecondary
                )
            )
        }

        Spacer(Modifier.height(12.dp))

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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(PlayerViewModel.msToString(position),
                    style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(PlayerViewModel.msToString(s.duration),
                    style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Playback Controls ─────────────────────────────────────
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically) {

            IconButton(onClick = { playerVM.toggleShuffle() }) {
                Icon(Icons.Default.Shuffle, "Shuffle",
                    tint = if (shuffle) HotPink else TextSecondary,
                    modifier = Modifier.size(28.dp))
            }
            Box(Modifier.size(52.dp).background(BorderBlack), Alignment.Center) {
                IconButton(onClick = { playerVM.skipPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, null,
                        tint = ElectricYellow, modifier = Modifier.size(36.dp))
                }
            }
            Box(
                Modifier.size(72.dp).background(ElectricYellow).border(3.dp, BorderBlack)
                    .clickable { playerVM.playPause() },
                Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    null, tint = BorderBlack, modifier = Modifier.size(44.dp)
                )
            }
            Box(Modifier.size(52.dp).background(BorderBlack), Alignment.Center) {
                IconButton(onClick = { playerVM.skipNext() }) {
                    Icon(Icons.Default.SkipNext, null,
                        tint = ElectricYellow, modifier = Modifier.size(36.dp))
                }
            }
            IconButton(onClick = { playerVM.toggleRepeat() }) {
                Icon(
                    when (repeatMode) {
                        Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        else -> Icons.Default.Repeat
                    },
                    "Repeat",
                    tint = if (repeatMode != Player.REPEAT_MODE_OFF) HotPink else TextSecondary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }

    // ── Speed Sheet ───────────────────────────────────────────────
    if (showSpeedSheet) {
        ModalBottomSheet(onDismissRequest = { showSpeedSheet = false },
            containerColor = Background) {
            Column(Modifier.padding(16.dp)) {
                Text("PLAYBACK SPEED", fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp, style = MaterialTheme.typography.titleMedium)
                if (slowedReverb) {
                    Text("Slowed+Reverb ON: pitch will follow speed",
                        style = MaterialTheme.typography.labelSmall, color = HotPink,
                        modifier = Modifier.padding(top = 4.dp))
                }
                Spacer(Modifier.height(12.dp))
                listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f).forEach { spd ->
                    TextButton(
                        onClick = { playerVM.setPlaybackSpeed(spd); showSpeedSheet = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${spd}x",
                            fontWeight = if (spd == speed) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (spd == speed) ElectricBlue else BorderBlack,
                            fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
