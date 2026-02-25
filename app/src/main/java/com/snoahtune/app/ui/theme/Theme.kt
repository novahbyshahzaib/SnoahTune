package com.snoahtune.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val neuColorScheme = lightColorScheme(
    background        = Background,
    surface           = SurfaceWhite,
    primary           = ElectricYellow,
    onPrimary         = BorderBlack,
    secondary         = HotPink,
    onSecondary       = SurfaceWhite,
    tertiary          = ElectricBlue,
    onTertiary        = SurfaceWhite,
    onBackground      = TextPrimary,
    onSurface         = TextPrimary,
    surfaceVariant    = Background,
    onSurfaceVariant  = TextSecondary,
    outline           = BorderBlack
)

private val neuTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 24.sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 18.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 14.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 12.sp,
                              fontFamily = FontFamily.Monospace)
)

@Composable
fun SnoahTuneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = neuColorScheme,
        typography  = neuTypography,
        content     = content
    )
}
