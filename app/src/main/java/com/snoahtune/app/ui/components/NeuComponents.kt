package com.snoahtune.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.snoahtune.app.ui.theme.*

/**
 * Neu Brutalism Card — white background, thick border, flat offset shadow
 */
@Composable
fun NeuCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val offsetX by animateDpAsState(targetValue = if (isPressed) 2.dp else 4.dp, label = "ox")
    val offsetY by animateDpAsState(targetValue = if (isPressed) 2.dp else 4.dp, label = "oy")

    Box(modifier = modifier) {
        // Shadow block
        Box(
            Modifier
                .matchParentSize()
                .offset(x = offsetX, y = offsetY)
                .background(BorderBlack)
        )
        // Card
        Column(
            Modifier
                .fillMaxWidth()
                .background(SurfaceWhite)
                .border(2.dp, BorderBlack)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .padding(12.dp),
            content = content
        )
    }
}

/**
 * Neu Brutalism Button — thick border, offset shadow, uppercase text
 */
@Composable
fun NeuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = ElectricYellow
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val ox by animateDpAsState(if (isPressed) 0.dp else 3.dp, label = "ox")
    val oy by animateDpAsState(if (isPressed) 0.dp else 3.dp, label = "oy")

    Box(modifier = modifier.height(IntrinsicSize.Min)) {
        Box(Modifier.matchParentSize().offset(x = ox, y = oy).background(BorderBlack))
        Box(
            Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .border(2.dp, BorderBlack)
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = BorderBlack
            )
        }
    }
}
