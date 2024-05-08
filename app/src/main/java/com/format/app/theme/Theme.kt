package com.format.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = ColorPalette.Primary,
    secondary = ColorPalette.Secondary,
    tertiary = ColorPalette.Tertiary
)

@Composable
fun ForMatTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = ColorPalette.Surface,
            darkIcons = true
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}