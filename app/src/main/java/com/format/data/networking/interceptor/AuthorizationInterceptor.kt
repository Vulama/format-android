package com.format.data.networking.interceptor

import com.format.common.infrastructure.logger.Logger
import com.format.data.networking.token.Token
import com.format.data.networking.token.TokenRepository
import com.format.data.networking.util.TokenRefresher
import com.format.data.networking.util.TokenValidityChecker
import com.format.domain.model.ApplicationFlows
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor(
    private val tokenRepository: TokenRepository,
    private val tokenValidityChecker: TokenValidityChecker,
    private val tokenRefresher: TokenRefresher,
    private val logger: Logger
) : Interceptor {

    private val refreshTokenLock: Any = Any()

    override fun intercept(chain: Chain): Response {
        val tokens = tokenRepository.get()
        val accessToken = tokens.accessToken
        logger.v(ApplicationFlows.NetworkingAuthorization, "Access token: $accessToken")

        if (accessToken.value.isNotBlank() && tokenValidityChecker.isValid(accessToken).not()) {
            logger.i(ApplicationFlows.NetworkingAuthorization, "Token expired, refreshing token")
            // Interception happens on a background thread and multiple threads could fire network request with outdated token
            // That is why we synchronize token refresh
            val refreshedToken = synchronized(refreshTokenLock) {
                val newAccessToken = tokenRepository.get().accessToken
                if (accessToken != newAccessToken) {
                    logger.i(ApplicationFlows.NetworkingAuthorization, "Token already refreshed")
                    // Access token has been refreshed by other thread
                    return@synchronized newAccessToken
                }

                // Refresh Token
                return@synchronized runBlocking {
                    tokenRefresher.refreshAndGetToken()
                }
            }

            logger.i(ApplicationFlows.NetworkingAuthorization, "Continuing with refreshed token")
            return chain.proceed(authorizedRequest(chain, refreshedToken))
        }

        return chain.proceed(authorizedRequest(chain, accessToken))
    }

    private fun authorizedRequest(chain: Chain, accessToken: Token): Request {
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${accessToken.value}")

        return requestBuilder.build()
    }
}