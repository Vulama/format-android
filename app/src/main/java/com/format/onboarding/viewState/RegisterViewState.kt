package com.format.onboarding.viewState

data class RegisterViewState(
    val isProcessing: Boolean = false,
    val errorMessage: String = "",
)