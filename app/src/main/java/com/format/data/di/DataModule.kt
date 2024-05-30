package com.format.data.di

import android.content.SharedPreferences
import com.format.common.di.getIODispatcher
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.data.infrastructure.logger.ForMatLogger
import com.format.common.infrastructure.logger.Logger
import com.format.data.api.RestrictedApi
import com.format.data.api.PublicApi
import com.format.data.formulas.repository.FormulasRepositoryImpl
import com.format.data.formulas.store.FormulaStoreImpl
import com.format.data.infrastructure.analytics.FirebaseAnalytics
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.infrastructure.preferences.ForMatPreferences
import com.format.data.networking.di.getAuthRetrofit
import com.format.data.networking.di.getGeneralRetrofit
import com.format.data.networking.token.TokenStore
import com.format.data.user.repository.UserRepositoryImpl
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.user.repository.UserRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.TimeZone

val dataModule = module {
    single<PublicApi> { getGeneralRetrofit().create(PublicApi::class.java) }

    single<RestrictedApi> { getAuthRetrofit().create(RestrictedApi::class.java) }

    single<Logger> {
        ForMatLogger(
            get<DateTimeProvider>(),
        )
    }

    single<SharedPreferences> {
        ForMatPreferences.get(androidApplication())
    }

    single<DateTimeProvider> {
        DateTimeProvider.Default(TimeZone.getDefault())
    }

    single<FormulasRepository> {
        FormulasRepositoryImpl(
            get<PublicApi>(),
            get<RestrictedApi>(),
            getIODispatcher(),
        )
    }

    single<UserRepository> {
        UserRepositoryImpl(
            get<PublicApi>(),
            getIODispatcher(),
            get<TokenStore>(),
            get<FormulasRepository>(),
            get<FormulaStore>(),
        )
    }

    single<FormulaStore> {
        FormulaStoreImpl(
            get<SharedPreferences>(),
        )
    }

    single<AnalyticsService> {
        FirebaseAnalytics()
    }
}