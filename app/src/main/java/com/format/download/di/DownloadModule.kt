package com.format.download.di

import com.format.app.navigation.navigator.Navigator
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.download.viewModel.DownloadFormulaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val downloadModule = module {
    viewModel<DownloadFormulaViewModel> {
        DownloadFormulaViewModel(
            get<FormulasRepository>(),
            get<FormulaStore>(),
            get<Navigator>(),
        )
    }
}