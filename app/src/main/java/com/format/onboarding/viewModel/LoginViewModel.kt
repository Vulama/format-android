package com.format.onboarding.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.common.model.AnalyticsScreen
import com.format.common.util.hashString
import com.format.data.api.PublicApi
import com.format.destinations.HomeScreenDestination
import com.format.domain.model.ApplicationFlows
import com.format.domain.user.repository.UserRepository
import com.format.onboarding.viewState.LoginViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
    private val logger: Logger,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginViewState())
    val uiState: LiveData<LoginViewState>
        get() = _uiState.asLiveData()

    init {
        analyticsService.trackScreen(AnalyticsScreen.LoginScreen)
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        logger.i(ApplicationFlows.Onboarding, "User login started")
        _uiState.update { it.copy(isProcessing = true) }
        userRepository.login(username, hashString(password)).fold(
            { appError ->
                logger.w(ApplicationFlows.Onboarding, "User login failed with message ${appError.message}")
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = appError.message
                    )
                }
            },
            {
                logger.i(ApplicationFlows.Onboarding, "User login succeeded")
                _uiState.update { it.copy(isProcessing = false) }
                navigator.navigate(HomeScreenDestination.route, true)
            }
        )
    }
}