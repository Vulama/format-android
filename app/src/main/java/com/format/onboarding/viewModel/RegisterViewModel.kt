package com.format.onboarding.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.model.AnalyticsScreen
import com.format.common.util.hashString
import com.format.destinations.HomeScreenDestination
import com.format.destinations.LoginScreenDestination
import com.format.domain.user.repository.UserRepository
import com.format.onboarding.viewState.LoginViewState
import com.format.onboarding.viewState.RegisterViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterViewState())
    val uiState: LiveData<RegisterViewState>
        get() = _uiState.asLiveData()

    init {
        analyticsService.trackScreen(AnalyticsScreen.RegisterScreen)
    }

    fun registerUser(username: String, password: String) = viewModelScope.launch {
        _uiState.update { it.copy(isProcessing = true) }
        userRepository.register(username, hashString(password)).fold(
            { appError ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = appError.message
                    )
                }
            },
            {
                userRepository.login(it.username, it.passwordHash).fold(
                    { appError ->
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                errorMessage = appError.message
                            )
                        }
                        navigator.navigate(LoginScreenDestination.route)
                    },
                    {
                        _uiState.update { it.copy(isProcessing = false) }
                        navigator.navigate(HomeScreenDestination.route, true)
                    }
                )
            }
        )
    }
}