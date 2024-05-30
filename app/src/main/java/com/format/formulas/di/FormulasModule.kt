package com.format.formulas.di

import com.format.app.navigation.navigator.Navigator
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.infrastructure.logger.Logger
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.TokenStore
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.formulas.viewModel.GroupDetailsViewModel
import com.format.formulas.viewModel.EditGroupViewModel
import com.format.formulas.viewModel.FormulaDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val formulasModule = module {
    viewModel<EditGroupViewModel> {
        EditGroupViewModel(
            get<FormulaStore>(),
            get<Navigator>(),
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }

    viewModel<GroupDetailsViewModel> {
        GroupDetailsViewModel(
            get<FormulaStore>(),
            get<FormulasRepository>(),
            get<Navigator>(),
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }

    viewModel<FormulaDetailsViewModel> {
        FormulaDetailsViewModel(
            get<TokenStore>(),
            get<DateTimeProvider>(),
            get<FormulasRepository>(),
            get<AnalyticsService>(),
            get<Logger>(),
        )
    }
}