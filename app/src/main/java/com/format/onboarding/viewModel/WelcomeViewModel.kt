package com.format.onboarding.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.format.app.navigation.navigator.Navigator
import com.format.onboarding.ui.destinations.LoginScreenDestination
import com.format.onboarding.ui.destinations.RegisterScreenDestination
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val navigator: Navigator,
) : ViewModel() {
    fun onLoginClicked() {
        navigator.navigate(LoginScreenDestination.route)
    }

    fun onRegisterClicked() {
        navigator.navigate(RegisterScreenDestination.route)
    }

    fun onContinueAsGuestClicked() {
        this.viewModelScope.launch {

        }
    }
}