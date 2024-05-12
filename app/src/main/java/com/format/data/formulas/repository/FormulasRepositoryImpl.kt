package com.format.data.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.common.model.toEpoch
import com.format.data.api.FormulaDto
import com.format.data.api.FormulaReactDto
import com.format.data.api.PublicApi
import com.format.data.api.PublishGroupDto
import com.format.data.api.ReactionsRequestDto
import com.format.data.api.RestrictedApi
import com.format.data.infrastructure.util.parseError
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FormulasRepositoryImpl(
    private val api: PublicApi,
    private val restrictedApi: RestrictedApi,
    private val ioDispatcher: CoroutineDispatcher,
) : FormulasRepository {
    override suspend fun groups(): Either<AppError, List<FormulaGroup>> = withContext(ioDispatcher) {
        Either.catch {
            api.groups().map {
                FormulaGroup(
                    name = it.name,
                    formulas = it.formulas.map { FormulaEntry(it.title, it.mathFormula, it.description, it.id ?: -1) },
                    id = it.id,
                )
            }
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override suspend fun publishGroup(formulaGroup: FormulaGroup): Either<AppError, FormulaGroup> = withContext(ioDispatcher) {
        Either.catch {
            val response = restrictedApi.publishGroup(
                PublishGroupDto(
                    name = formulaGroup.name,
                    formulas = formulaGroup.formulas.map { FormulaDto(it.title, it.formula, it.description ) }
                )
            )
            formulaGroup.copy(id = response.group.id)
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override suspend fun getReactions(formulaGroup: FormulaGroup): Either<AppError, List<Reaction>> = withContext(ioDispatcher) {
        Either.catch {
            val response = restrictedApi.reactions(ReactionsRequestDto(formulaGroup.id))
            response.reactions.map { Reaction(it.responseType.toBooleanStrict(), it.createdAt.toEpoch(), it.formulaId) }
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override suspend fun react(formulaEntry: FormulaEntry, responseType: Boolean): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            restrictedApi.formulaReact(FormulaReactDto(formulaEntry.id, responseType.toString()))
            Unit
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }
}