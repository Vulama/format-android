package com.format.common.di

import com.format.common.infrastructure.configuration.Configuration
import com.format.common.infrastructure.configuration.DevConfiguration
import com.format.common.infrastructure.configuration.ProdConfiguration
import com.format.format.BuildConfig
import org.koin.dsl.module

val commonModule = module {
    single<Configuration> {
        if (BuildConfig.FLAVOR == "prod") {
            ProdConfiguration
        } else {
            DevConfiguration
        }
    }
}