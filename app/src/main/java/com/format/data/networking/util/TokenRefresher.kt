package com.format.data.networking.util

import com.format.common.model.Epoch
import com.format.common.model.toEpoch
import com.format.data.api.PublicApi
import com.format.data.api.RefreshTokenRequest
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface TokenRefresher {
    suspend fun refreshAndGetToken(): Token

    class Default(
        private val ioDispatcher: CoroutineDispatcher,
        private val tokenRepository: TokenRepository,
        private val publicApi: PublicApi,
    ) : TokenRefresher {
        override suspend fun refreshAndGetToken(): Token = withContext(ioDispatcher) {
            val refreshToken = tokenRepository.get().refreshToken
            val refreshDto = publicApi.refreshToken(RefreshTokenRequest(refreshToken.value))
            val newAccessToken = Token(refreshDto.accessToken, refreshDto.accessTokenExpiresAt.toEpoch())
            tokenRepository.set(newAccessToken, refreshToken)
            newAccessToken
        }
    }
}