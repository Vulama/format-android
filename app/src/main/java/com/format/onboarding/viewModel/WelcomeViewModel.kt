package com.format.onboarding.viewModel

import androidx.lifecycle.ViewModel
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.model.AnalyticsScreen
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
    private val analyticsService: AnalyticsService,
) : ViewModel() {
    init {
        if (tokenStore.get().refreshToken.expiresAt > dateTimeProvider.now().plusHours(24)) {
            navigator.navigate(HomeScreenDestination.route, true)
        } else {
            analyticsService.trackScreen(AnalyticsScreen.WelcomeScreen)
        }
    }

    fun onLoginClicked() {
        navigator.navigate(LoginScreenDestination.route)
    }

    fun onRegisterClicked() {
        navigator.navigate(RegisterScreenDestination.route)
    }
}