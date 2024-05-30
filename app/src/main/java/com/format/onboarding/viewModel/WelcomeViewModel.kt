package com.format.onboarding.viewModel

import androidx.lifecycle.ViewModel
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.common.model.plusHours
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.TokenStore
import com.format.destinations.HomeScreenDestination
import com.format.destinations.LoginScreenDestination
import com.format.destinations.RegisterScreenDestination
import com.format.domain.model.ApplicationFlows

class WelcomeViewModel(
    private val navigator: Navigator,
    private val tokenStore: TokenStore,
    private val dateTimeProvider: DateTimeProvider,
    private val analyticsService: AnalyticsService,
    private val logger: Logger,
) : ViewModel() {
    init {
        if (tokenStore.get().refreshToken.expiresAt > dateTimeProvider.now().plusHours(24)) {
            logger.i(ApplicationFlows.Onboarding, "User navigated to home screen")
            navigator.navigate(HomeScreenDestination.route, true)
        } else {
            analyticsService.trackScreen(AnalyticsScreen.WelcomeScreen)
        }
    }

    fun onLoginClicked() {
        logger.i(ApplicationFlows.Onboarding, "User proceeded to login")
        navigator.navigate(LoginScreenDestination.route)
    }

    fun onRegisterClicked() {
        logger.i(ApplicationFlows.Onboarding, "User proceeded to register")
        navigator.navigate(RegisterScreenDestination.route)
    }
}