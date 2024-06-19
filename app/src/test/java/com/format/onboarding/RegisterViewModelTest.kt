package com.format.onboarding

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import arrow.core.left
import arrow.core.right
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AppError
import com.format.destinations.HomeScreenDestination
import com.format.destinations.LoginScreenDestination
import com.format.domain.model.User
import com.format.domain.user.repository.UserRepository
import com.format.onboarding.viewModel.RegisterViewModel
import com.format.onboarding.viewState.RegisterViewState
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RegisterViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var userRepository: UserRepository
    private lateinit var navigator: Navigator
    private lateinit var analyticsService: AnalyticsService
    private lateinit var logger: Logger

    private lateinit var viewModel: RegisterViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        navigator = mockk(relaxed = true)
        analyticsService = mockk(relaxed = true)
        logger = mockk(relaxed = true)

        viewModel = RegisterViewModel(userRepository, navigator, analyticsService, logger)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `registerUser should update state correctly on successful registration and login`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpassword"

        coEvery { userRepository.register(username, any()) } returns User.Empty.copy(username = username).right()
        coEvery { userRepository.login(username, any()) } returns Unit.right()

        // When
        viewModel.registerUser(username, password)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.uiState.value?.isProcessing)
        assertEquals("", viewModel.uiState.value?.errorMessage)
        verify {
            navigator.navigate(HomeScreenDestination.route, true)
        }
    }

    @Test
    fun `registerUser should update state correctly on registration failure`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpassword"
        val errorMessage = "Registration failed"

        coEvery { userRepository.register(username, any()) } returns AppError.ApiError(errorMessage).left()

        // When
        viewModel.registerUser(username, password)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.uiState.value?.isProcessing)
        assertEquals(errorMessage, viewModel.uiState.value?.errorMessage)
        verify(exactly = 0) {
            navigator.navigate(HomeScreenDestination.route, true)
        }
    }

    @Test
    fun `registerUser should update state correctly on login failure after successful registration`() = runTest {
        // Given
        val username = "testuser"
        val password = "testpassword"
        val errorMessage = "Login failed"

        coEvery { userRepository.register(username, any()) } returns User.Empty.copy(username = username).right()
        coEvery { userRepository.login(username, any()) } returns AppError.ApiError(errorMessage).left()

        // When
        viewModel.registerUser(username, password)
        advanceUntilIdle()

        // Then
        assertEquals(false, viewModel.uiState.value?.isProcessing)
        assertEquals(errorMessage, viewModel.uiState.value?.errorMessage)
        verify(exactly = 0) {
            navigator.navigate(HomeScreenDestination.route)
        }
    }
}
