package com.format.data.user.repository

import arrow.core.Either
import arrow.core.continuations.either
import com.format.common.model.AppError
import com.format.common.model.toEpoch
import com.format.data.api.PublicApi
import com.format.data.api.LoginRequest
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenRepository
import com.format.data.networking.token.Tokens
import com.format.domain.user.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val api: PublicApi,
    private val ioDispatcher: CoroutineDispatcher,
    private val tokenRepository: TokenRepository,
) : UserRepository {
    override suspend fun login(username: String, password: String): Either<AppError, Unit> = withContext(ioDispatcher) {
        Either.catch {
            val tokensDto = api.login(LoginRequest(username, password))
            val tokens = Tokens(
                accessToken = Token(tokensDto.accessToken, tokensDto.accessTokenExpiresAt.toEpoch()),
                refreshToken = Token(tokensDto.refreshToken, tokensDto.refreshTokenExpiresAt.toEpoch()),
            )
            tokenRepository.set(tokens.accessToken, tokens.refreshToken)
        }.mapLeft {
            AppError.ApiError
        }
    }
}