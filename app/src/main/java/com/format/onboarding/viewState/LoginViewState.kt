package com.format.onboarding.viewState

data class LoginViewState(
    val isProcessing: Boolean = false,
    val errorMessage: String = "",
)