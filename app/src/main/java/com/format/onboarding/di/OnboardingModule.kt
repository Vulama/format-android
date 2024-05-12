package com.format.onboarding.di

import com.format.app.navigation.navigator.Navigator
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.TokenStore
import com.format.domain.user.repository.UserRepository
import com.format.onboarding.viewModel.LoginViewModel
import com.format.onboarding.viewModel.RegisterViewModel
import com.format.onboarding.viewModel.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel<WelcomeViewModel> {
        WelcomeViewModel(
            get<Navigator>(),
            get<TokenStore>(),
            get<DateTimeProvider>(),
        )
    }

    viewModel<LoginViewModel> {
        LoginViewModel(
            get(),
            get<UserRepository>(),
            get<Navigator>(),
        )
    }

    viewModel<RegisterViewModel> {
        RegisterViewModel(
            get<UserRepository>(),
            get<Navigator>(),
        )
    }
}