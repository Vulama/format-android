package com.format.data.user.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.common.model.toEpoch
import com.format.data.api.PublicApi
import com.format.data.api.LoginRequest
import com.format.data.api.RegisterRequest
import com.format.data.networking.util.parseError
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenStore
import com.format.data.networking.token.Tokens
import com.format.domain.formulas.repository.FormulasRepository
import com.format.domain.formulas.store.FormulaStore
import com.format.domain.model.User
import com.format.domain.user.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.math.log

class UserRepositoryImpl(
    private val api: PublicApi,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenStore: TokenStore,
    private val formulasRepository: FormulasRepository,
    private val formulaStore: FormulaStore,
) : UserRepository {
    override suspend fun login(username: String, passwordHash: String): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            val response = api.login(LoginRequest(username, passwordHash))
            val loginResponseDto = response.body()
            if (response.isSuccessful.not() || loginResponseDto == null) throw response.parseError()
            val tokens = Tokens(
                accessToken = Token(loginResponseDto.accessToken, loginResponseDto.accessTokenExpiresAt.toEpoch()),
                refreshToken = Token(loginResponseDto.refreshToken, loginResponseDto.refreshTokenExpiresAt.toEpoch()),
            )
            formulasRepository.setDownloadedFormulas(loginResponseDto.downloadedFormulaGroups.mapNotNull { it.formulaGroup })
            formulasRepository.setUserReactions(loginResponseDto.userReactions)
            tokenStore.set(tokens.accessToken, tokens.refreshToken)
            formulaStore.setKey(loginResponseDto.user.username)
        }.mapLeft {
            AppError.ApiError(it.message ?: "Api Error")
        }
    }

    override suspend fun register(username: String, passwordHash: String): Either<AppError, User> = withContext(ioDispatcher) {
        Either.catch {
            val response = api.register(RegisterRequest(username, passwordHash))
            val userDto = response.body()?.user
            if (response.isSuccessful.not() || userDto == null) throw response.parseError()
            User(userDto.id, userDto.username, userDto.passwordHash)
        }.mapLeft {
            AppError.ApiError(it.message ?: "Api Error")
        }
    }
}