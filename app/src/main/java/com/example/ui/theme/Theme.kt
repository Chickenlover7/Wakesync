package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val NeoColorScheme = lightColorScheme(
    primary = ZeroPink,
    onPrimary = ZeroWhite,
    secondary = ZeroMint,
    onSecondary = ZeroNavy,
    tertiary = ZeroWhite,
    onTertiary = ZeroNavy,
    background = ZeroCream,
    onBackground = ZeroNavy,
    surface = ZeroWhite,
    onSurface = ZeroNavy,
    surfaceVariant = ZeroWhite,
    onSurfaceVariant = ZeroNavy,
    error = ZeroPink,
    onError = ZeroWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep it light for Neo-Brutalism aesthetics
    dynamicColor: Boolean = false, // Disable dynamic color to enforce theme
    content: @Composable () -> Unit,
) {
    MaterialTheme(colorScheme = NeoColorScheme, typography = Typography, content = content)
}
