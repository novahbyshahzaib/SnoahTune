package com.snoahtune.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import coil.compose.SubcomposeAsyncImage
import com.snoahtune.app.domain.model.Song
import com.snoahtune.app.ui.theme.*

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean = false,
    onClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeuCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Album art with fallback music note icon
            Box(
                Modifier
                    .size(56.dp)
                    .border(2.dp, BorderBlack)
                    .background(ElectricYellow),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = "content://media/external/audio/albumart/${song.albumId}",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        // Show music note while loading
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = BorderBlack,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    error = {
                        // Show music note if no album art exists
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = BorderBlack,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                )
                // Playing indicator overlay
                if (isPlaying) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(ElectricYellow.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = BorderBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isPlaying) ElectricBlue else TextPrimary
                )
                Text(
                    text = "${song.artist} · ${msToStr(song.duration)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onMoreClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = BorderBlack)
            }
        }
    }
}

private fun msToStr(ms: Long): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}
