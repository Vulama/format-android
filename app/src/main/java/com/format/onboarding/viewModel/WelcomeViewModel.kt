package com.format.onboarding.viewModel

import androidx.lifecycle.ViewModel
import com.format.app.navigation.navigator.Navigator
import com.format.common.model.plusHours
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.TokenStore
import com.format.destinations.HomeScreenDestination
import com.format.destinations.LoginScreenDestination
import com.format.destinations.RegisterScreenDestination

class WelcomeViewModel(
    private val navigator: Navigator,
    private val tokenStore: TokenStore,
    private val dateTimeProvider: DateTimeProvider,
) : ViewModel() {
    init {
        if (tokenStore.get().refreshToken.expiresAt > dateTimeProvider.now().plusHours(24)) {
            navigator.navigate(HomeScreenDestination.route, true)
        }
    }

    fun onLoginClicked() {
        navigator.navigate(LoginScreenDestination.route)
    }

    fun onRegisterClicked() {
        navigator.navigate(RegisterScreenDestination.route)
    }

    fun onContinueAsGuestClicked() {
        navigator.navigate(HomeScreenDestination.route, true)
    }
}