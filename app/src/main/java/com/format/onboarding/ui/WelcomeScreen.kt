package com.format.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.format.app.theme.ColorPalette
import com.format.common.ui.VerticalSpacer
import com.format.R
import com.format.common.ui.NormalButton
import com.format.onboarding.viewModel.WelcomeViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.koin.androidx.compose.getViewModel

@Destination
@RootNavGraph(start = true)
@Composable
fun WelcomeScreen() {
    val viewModel: WelcomeViewModel = getViewModel()

    WelcomeScreenStateless(
        onLoginClicked = { viewModel.onLoginClicked() },
        onRegisterClicked = { viewModel.onRegisterClicked() },
    )
}


@Composable
private fun WelcomeScreenStateless(
    onLoginClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {

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
            Box(modifier = Modifier.size(120.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(36.dp)),
                    contentDescription = null,
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    modifier = Modifier.size(120.dp),
                    tint = ColorPalette.OnPrimary,
                    contentDescription = null,
                )
            }


            VerticalSpacer(distance = 16.dp)

            Text(
                text = stringResource(id = R.string.welcome_screen_title),
                style = MaterialTheme.typography.headlineMedium,
            )
        }


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NormalButton(
                onClick = { onLoginClicked() },
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_screen_login_button),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            VerticalSpacer(distance = 8.dp)

            NormalButton(
                onClick = { onRegisterClicked() },
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_screen_register_button),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreenStateless({}, {})
}
