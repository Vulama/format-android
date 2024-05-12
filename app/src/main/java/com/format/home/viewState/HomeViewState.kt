package com.format.home.viewState

import com.format.domain.model.FormulaGroup

data class HomeViewState(
    val favouriteFormulas: List<FormulaGroup> = emptyList(),
    val localFormulas: List<FormulaGroup> = emptyList(),
    val remoteFormulas: List<FormulaGroup> = emptyList(),
)