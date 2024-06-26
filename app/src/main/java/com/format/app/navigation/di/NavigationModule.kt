package com.format.app.navigation.di

import com.format.app.navigation.controller.NavHostControllerProvider
import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.logger.Logger
import org.koin.dsl.module

val navigationModule = module {
    single<NavHostControllerProvider> {
        NavHostControllerProvider.Default(
            get<Logger>(),
        )
    }

    single<Navigator> {
        Navigator.Default(
            get<NavHostControllerProvider>(),
        )
    }
}