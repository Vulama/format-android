package com.format.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.common.model.toEpoch
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenStore
import com.format.data.networking.token.Tokens
import com.format.destinations.HomeScreenDestination
import com.format.destinations.LoginScreenDestination
import com.format.destinations.RegisterScreenDestination
import com.format.domain.model.ApplicationFlows
import com.format.onboarding.viewModel.WelcomeViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class WelcomeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var navigator: Navigator
    private lateinit var tokenStore: TokenStore
    private lateinit var dateTimeProvider: DateTimeProvider
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger

    private lateinit var viewModel: WelcomeViewModel

    @Before
    fun setup() {
        navigator = mockk(relaxed = true)
        tokenStore = mockk()
        dateTimeProvider = mockk()
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        every { tokenStore.get() } returns Tokens(
            Token.Empty,
            Token.Empty,
        )
        every { dateTimeProvider.now() } returns LocalDateTime.now().toString().toEpoch()

        viewModel = WelcomeViewModel(navigator, tokenStore, dateTimeProvider, analyticsService, logger)
    }

    @Test
    fun `init should navigate to home screen if refreshToken is valid for more than 24 hours`() {
        // Given
        every { tokenStore.get().refreshToken.expiresAt } returns LocalDateTime.now().plusHours(48).toString().toEpoch()
        every { dateTimeProvider.now() } returns LocalDateTime.now().toString().toEpoch()

        // When
        viewModel = WelcomeViewModel(navigator, tokenStore, dateTimeProvider, analyticsService, logger)

        // Then
        verify {
            logger.i(ApplicationFlows.Onboarding, "User navigated to home screen")
            navigator.navigate(HomeScreenDestination.route, true)
        }
    }

    @Test
    fun `init should track WelcomeScreen if refreshToken is not valid for more than 24 hours`() {
        // Given
        every { tokenStore.get().refreshToken.expiresAt } returns LocalDateTime.now().plusHours(12).toString().toEpoch()
        every { dateTimeProvider.now() } returns LocalDateTime.now().toString().toEpoch()

        // When
        viewModel = WelcomeViewModel(navigator, tokenStore, dateTimeProvider, analyticsService, logger)

        // Then
        verify {
            analyticsService.trackScreen(AnalyticsScreen.WelcomeScreen)
        }
    }

    @Test
    fun `onLoginClicked should navigate to login screen`() {
        // When
        viewModel.onLoginClicked()

        // Then
        verify {
            logger.i(ApplicationFlows.Onboarding, "User proceeded to login")
            navigator.navigate(LoginScreenDestination.route)
        }
    }

    @Test
    fun `onRegisterClicked should navigate to register screen`() {
        // When
        viewModel.onRegisterClicked()

        // Then
        verify {
            logger.i(ApplicationFlows.Onboarding, "User proceeded to register")
            navigator.navigate(RegisterScreenDestination.route)
        }
    }
}
