package com.format.onboarding.di

import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
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
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }

    viewModel<LoginViewModel> {
        LoginViewModel(
            get<UserRepository>(),
            get<Navigator>(),
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }

    viewModel<RegisterViewModel> {
        RegisterViewModel(
            get<UserRepository>(),
            get<Navigator>(),
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }
}