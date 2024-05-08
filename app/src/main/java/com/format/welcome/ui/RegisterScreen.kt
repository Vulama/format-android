package com.format.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.format.app.theme.ColorPalette
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun RegisterScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.SecondaryContainer)
    ) {
        Button(
            onClick = {},
        ) {
            Text(text = "Switch")
        }
    }
}