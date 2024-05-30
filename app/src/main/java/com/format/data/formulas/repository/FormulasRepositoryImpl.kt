package com.format.data.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.common.model.toEpoch
import com.format.data.api.FormulaDto
import com.format.data.api.FormulaReactDto
import com.format.data.api.GroupDto
import com.format.data.api.PublicApi
import com.format.data.api.PublishGroupDto
import com.format.data.api.ReactionDto
import com.format.data.api.ReactionsRequestDto
import com.format.data.api.RemoteFormulaRequest
import com.format.data.api.RestrictedApi
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
    private var _userReactions = mutableListOf<Reaction>()
    private var _downloadedFormulas = mutableListOf<FormulaGroup>()

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
                    formulas = formulaGroup.formulas.map { FormulaDto(it.title, it.formula, it.description, it.id) }
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

    override suspend fun downloadFormulaGroup(groupId: Int): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            restrictedApi.groupDownloaded(RemoteFormulaRequest(groupId))
            Unit
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override suspend fun deleteRemoteGroup(groupId: Int): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            restrictedApi.groupDeleted(RemoteFormulaRequest(groupId))
            Unit
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override suspend fun updateUserData(): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            val data = restrictedApi.loadUserData()
            setDownloadedFormulas(data.downloadedFormulaGroups.mapNotNull { it.formulaGroup })
            setUserReactions(data.userReactions)
        }.mapLeft {
            AppError.ApiError(it.message ?: "Error")
        }
    }

    override fun userReactions(): List<Reaction> = _userReactions

    override fun downloadedFormulas(): List<FormulaGroup> = _downloadedFormulas

    override fun updateUserReactions(userReaction: Reaction) {
        _userReactions.updateOrAdd(userReaction) { it.formulaId }
    }

    override fun updateDownloadedFormulas(downloadedFormulaGroup: FormulaGroup) {
        _downloadedFormulas.updateOrAdd(downloadedFormulaGroup) { it.id }
    }

    override fun deleteUserReactions(userReaction: Reaction) {
        _userReactions.removeIf { it.formulaId == userReaction.formulaId }
    }

    override fun deleteDownloadedFormulas(downloadedFormulaGroup: FormulaGroup) {
        _downloadedFormulas.removeIf { it.id == downloadedFormulaGroup.id }
    }

    override fun setUserReactions(userReactions: List<ReactionDto>) {
        _userReactions = userReactions.map {
            Reaction(
                type = it.responseType.toBooleanStrictOrNull() ?: false,
                createdAt = it.createdAt.toEpoch(),
                formulaId = it.formulaId,
            )
        }.toMutableList()
    }

    override fun setDownloadedFormulas(downloadedFormulaGroups: List<GroupDto>) {
        _downloadedFormulas = downloadedFormulaGroups.map {
            FormulaGroup(
                name = it.name,
                formulas = it.formulas.map { FormulaEntry(it.title, it.mathFormula, it.description, it.id ?: -1) },
                id = it.id,
            )
        }.toMutableList()
    }

    private inline fun <reified T> MutableList<T>.updateOrAdd(newObj: T, idSelector: (T) -> Any) {
        val index = indexOfFirst { idSelector(it) == idSelector(newObj) }
        if (index != -1) {
            this[index] = newObj
        } else {
            add(newObj)
        }
    }
}