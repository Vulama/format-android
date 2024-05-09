package com.format.domain.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.domain.model.FormulaGroup

interface FormulasRepository {
    suspend fun groups(): Either<AppError, List<FormulaGroup>>
}