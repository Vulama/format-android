package com.format.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.format.app.navigation.controller.NavHostControllerProvider
import com.format.app.theme.ColorPalette
import com.format.welcome.ui.destinations.RegisterScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.get

@Destination
@Composable
fun LoginScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.PrimaryContainer)
    ) {
        Button(
            onClick = {},
        ) {
            Text(text = "Switch")
        }
    }
}