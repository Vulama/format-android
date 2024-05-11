package com.format.onboarding.di

import com.format.app.navigation.navigator.Navigator
import com.format.onboarding.viewModel.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val onboardingModule = module {
    viewModel<WelcomeViewModel> {
        WelcomeViewModel(
            get<Navigator>(),
        )
    }
}