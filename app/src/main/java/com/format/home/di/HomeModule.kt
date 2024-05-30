package com.format.home.di

import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.data.networking.token.TokenStore
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.home.viewModel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel<HomeViewModel> {
        HomeViewModel(
            get<Navigator>(),
            get<FormulaStore>(),
            get<TokenStore>(),
            get<FormulasRepository>(),
            get<AnalyticsService>(),
        )
    }
}