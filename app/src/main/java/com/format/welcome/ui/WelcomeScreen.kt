package com.format.welcome.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette
import com.format.common.ui.VerticalSpacer
import com.format.format.R
import com.ramcosta.composedestinations.annotation.Destination

@Destination(start = true)
@Composable
fun WelcomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorPalette.PrimaryContainer.copy(0.15f)),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .size(120.dp)
                    .background(ColorPalette.Primary, RoundedCornerShape(36.dp)),
                tint = ColorPalette.OnPrimary,
                contentDescription = null,
            )

            VerticalSpacer(distance = 16.dp)

            Text(
                text = "Welcome to ForMat",
                style = MaterialTheme.typography.headlineMedium,
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {},
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            VerticalSpacer(distance = 8.dp)

            Button(
                onClick = {},
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            VerticalSpacer(distance = 16.dp)

            Text(
                text = "Continue as Guest",
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}
