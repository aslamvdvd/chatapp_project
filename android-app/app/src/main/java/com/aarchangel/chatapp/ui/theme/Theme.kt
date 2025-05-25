package com.aarchangel.chatapp.ui.theme

// ChatApp by aarchangel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import android.os.Build
import androidx.compose.ui.graphics.Color // Ensure Color is imported for KDoc if needed, though not directly used in this file's public API params
import androidx.compose.ui.platform.LocalContext

/**
 * Dark color scheme for the ChatApp theme.
 * Defines primary, secondary, and tertiary colors for dark mode.
 * // ChatApp by aarchangel
 */
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

/**
 * Light color scheme for the ChatApp theme.
 * Defines primary, secondary, and tertiary colors for light mode.
 * // ChatApp by aarchangel
 */
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * The main Composable theme for the ChatApp application.
 * This function applies Material Design 3 styling, including color schemes and typography.
 * It supports system dark mode and dynamic coloring on Android 12+.
 * // ChatApp by aarchangel
 *
 * @param darkTheme Whether the theme should be dark. Defaults to the system's dark mode setting.
 * @param dynamicColor Whether to enable dynamic coloring (Material You) on supported devices (Android 12+).
 *                     Defaults to true. Has no effect on unsupported devices.
 * @param content The Composable content to which this theme will be applied.
 */
@Composable
fun ChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 