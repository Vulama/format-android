package com.format.data.networking.util

import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.token.Token

interface TokenValidityChecker {

    fun isValid(
        token: Token
    ): Boolean

    class Default(
        private val dateTimeProvider: DateTimeProvider,
    ) : TokenValidityChecker {
        override fun isValid(token: Token): Boolean {
            if (token.value.isEmpty() || token.expiresAt.value <= 0) {
                return false
            }

            return (token.expiresAt.value - dateTimeProvider.now().value) > 300L // 5min
        }
    }
}