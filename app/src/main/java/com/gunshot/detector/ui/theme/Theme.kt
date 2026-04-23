package com.gunshot.detector.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val AlertRed = Color(0xFFE53935)
val AlertRedDark = Color(0xFFB71C1C)
val BackgroundDark = Color(0xFF0A0A0F)
val SurfaceDark = Color(0xFF141420)
val SurfaceVariant = Color(0xFF1E1E2E)
val OnSurface = Color(0xFFE0E0E0)
val OnSurfaceMuted = Color(0xFF7070A0)

private val DarkColorScheme = darkColorScheme(
    primary = AlertRed,
    onPrimary = Color.White,
    secondary = AlertRedDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariant,
    onBackground = OnSurface,
    onSurface = OnSurface,
)

@Composable
fun GunShotDetectorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}