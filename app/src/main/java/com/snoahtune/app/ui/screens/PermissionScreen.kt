package com.snoahtune.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.snoahtune.app.ui.components.NeuButton
import com.snoahtune.app.ui.components.NeuCard
import com.snoahtune.app.ui.theme.*

@Composable
fun PermissionScreen(onRequestPermission: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo block
        Box(
            Modifier
                .size(120.dp)
                .background(ElectricYellow)
                .border(3.dp, BorderBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = BorderBlack
            )
        }

        Spacer(Modifier.height(28.dp))

        Text(
            "SNOAHTUNE",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "NEU BRUTALISM MUSIC PLAYER",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        NeuCard {
            Text(
                "MUSIC ACCESS REQUIRED",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "SnoahTune needs access to your device's audio files to scan and play your music library. " +
                "Your music never leaves your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Spacer(Modifier.height(24.dp))

        NeuButton(
            text = "Grant Permission",
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
