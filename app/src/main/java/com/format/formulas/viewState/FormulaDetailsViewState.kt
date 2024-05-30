package com.format.formulas.viewState

import com.format.domain.model.Reaction

data class FormulaDetailsViewState(
    val areReactionsEnabled: Boolean,
    val reaction: Reaction?,
)