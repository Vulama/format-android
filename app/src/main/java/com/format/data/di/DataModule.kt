package com.format.data.di

import com.format.data.infrastructure.ForMatLogger
import com.format.common.infrastructure.logger.Logger
import org.koin.dsl.module

val dataModule = module {
    single<Logger> {
        ForMatLogger()
    }
}