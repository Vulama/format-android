package com.format.data.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.data.api.PublicApi
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.FormulaGroup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FormulasRepositoryImpl(
    private val api: PublicApi,
    private val ioDispatcher: CoroutineDispatcher,
) : FormulasRepository {
    override suspend fun groups(): Either<AppError, List<FormulaGroup>> = withContext(ioDispatcher) {
        Either.catch {
            api.groups().map {
                FormulaGroup(it.name)
            }
        }.mapLeft { AppError.ApiError }
    }
}