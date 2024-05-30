package com.format.domain.formulas.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.data.api.GroupDto
import com.format.data.api.ReactionDto
import com.format.domain.model.FormulaEntry
import com.format.domain.model.FormulaGroup
import com.format.domain.model.Reaction

interface FormulasRepository {
    suspend fun groups(): Either<AppError, List<FormulaGroup>>

    suspend fun publishGroup(formulaGroup: FormulaGroup): Either<AppError, FormulaGroup>

    suspend fun getReactions(formulaGroup: FormulaGroup): Either<AppError, List<Reaction>>

    suspend fun react(formulaEntry: FormulaEntry, responseType: Boolean): Either<AppError, Unit>

    suspend fun downloadFormulaGroup(groupId: Int): Either<AppError, Unit>

    suspend fun deleteRemoteGroup(groupId: Int): Either<AppError, Unit>

    suspend fun updateUserData(): Either<AppError, Unit>

    fun userReactions(): List<Reaction>

    fun downloadedFormulas(): List<FormulaGroup>

    fun updateUserReactions(userReaction: Reaction)

    fun updateDownloadedFormulas(downloadedFormulaGroup: FormulaGroup)

    fun deleteUserReactions(userReaction: Reaction)

    fun deleteDownloadedFormulas(downloadedFormulaGroup: FormulaGroup)

    fun setUserReactions(userReactions: List<ReactionDto>)

    fun setDownloadedFormulas(downloadedFormulaGroups: List<GroupDto>)
}