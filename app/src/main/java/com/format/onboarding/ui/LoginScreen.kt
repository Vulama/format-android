package com.format.onboarding.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.format.R
import com.format.app.theme.ColorPalette
import com.format.common.ui.ForMatInputField
import com.format.common.ui.NormalButton
import com.format.common.ui.Spinner
import com.format.common.ui.VerticalSpacer
import com.format.onboarding.viewModel.LoginViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun LoginScreen() {
    val viewModel: LoginViewModel = getViewModel()
    val viewState = viewModel.uiState.observeAsState().value

    viewState?.let {
        LoginScreenStateless(
            isProcessing = viewState.isProcessing,
            message = viewState.errorMessage,
            onContinueClicked = { username, password -> viewModel.loginUser(username, password) }
        )
    }
}

@Composable
private fun LoginScreenStateless(
    isProcessing: Boolean,
    message: String,
    onContinueClicked: (String, String) -> Unit,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.login_screen_title),
            style = MaterialTheme.typography.headlineLarge,
        )

        if (isProcessing) {
            Spinner()
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ForMatInputField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(id = R.string.login_screen_username_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            VerticalSpacer(16.dp)

            ForMatInputField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(id = R.string.login_screen_password_hint)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
            )

            VerticalSpacer(distance = 8.dp)

            Text(
                text = message,
                color = ColorPalette.Error,
                style = MaterialTheme.typography.labelMedium,
            )

            VerticalSpacer(distance = 8.dp)

            NormalButton(
                onClick = { onContinueClicked(username, password) },
            ) {
                Text(
                    text = stringResource(id = R.string.login_screen_button_label),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }
    }
}