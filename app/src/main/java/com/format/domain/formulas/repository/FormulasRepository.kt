package com.format.domain.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction

interface FormulasRepository {
    suspend fun groups(): Either<AppError, List<FormulaGroup>>

    suspend fun publishGroup(formulaGroup: FormulaGroup) : Either<AppError, FormulaGroup>

    suspend fun getReactions(formulaGroup: FormulaGroup) : Either<AppError, List<Reaction>>

    suspend fun react(formulaEntry: FormulaEntry, responseType: Boolean) : Either<AppError, Unit>
}