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
import com.format.data.api.PublicApi
import com.format.destinations.HomeScreenDestination
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.user.repository.UserRepository
import com.format.onboarding.viewState.LoginViewState
import com.format.onboarding.viewState.RegisterViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val api: PublicApi,
    private val userRepository: UserRepository,
    private val navigator: Navigator,
    private val analyticsService: AnalyticsService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginViewState())
    val uiState: LiveData<LoginViewState>
        get() = _uiState.asLiveData()

    init {
        analyticsService.trackScreen(AnalyticsScreen.LoginScreen)
    }

    fun loginUser(username: String, password: String) = viewModelScope.launch {
        _uiState.update { it.copy(isProcessing = true) }
        userRepository.login(username, hashString(password)).fold(
            { appError ->
                _uiState.update {
                    it.copy(
                        isProcessing = false,
                        errorMessage = appError.message
                    )
                }
            },
            {
                _uiState.update { it.copy(isProcessing = false) }
                navigator.navigate(HomeScreenDestination.route, true)
            }
        )
    }
}