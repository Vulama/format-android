package com.format.data.networking.token

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val ACCESS_TOKEN_PREFERENCE_KEY = "access-token-preference-key"
private const val REFRESH_TOKEN_PREFERENCE_KEY = "refresh-token-preference-key"

interface TokenRepository {

    fun get(): Tokens

    fun set(accessToken: Token, refreshToken: Token?)

    fun drop()

    class Default(
        private val sharedPreferences: SharedPreferences,
    ) : TokenRepository {
        override fun get() = try {
            val tokenAsString = sharedPreferences.getString(ACCESS_TOKEN_PREFERENCE_KEY, emptyTokenSerialized) ?: emptyTokenSerialized
            val refreshTokenAsString = sharedPreferences.getString(REFRESH_TOKEN_PREFERENCE_KEY, emptyTokenSerialized) ?: emptyTokenSerialized
            Tokens(
                Json.decodeFromString(tokenAsString),
                Json.decodeFromString(refreshTokenAsString),
            )
        } catch (_: Exception) {
            Tokens(Token.Empty, Token.Empty)
        }

        override fun set(accessToken: Token, refreshToken: Token?) = sharedPreferences.edit {
            val oldRefreshTokenAsString = sharedPreferences.getString(ACCESS_TOKEN_PREFERENCE_KEY, emptyTokenSerialized) ?: emptyTokenSerialized
            val oldRefreshToken = Json.decodeFromString<Token>(oldRefreshTokenAsString)
            putString(ACCESS_TOKEN_PREFERENCE_KEY, Json.encodeToString(accessToken))
            putString(REFRESH_TOKEN_PREFERENCE_KEY, Json.encodeToString(refreshToken ?: oldRefreshToken))
        }

        override fun drop() = set(Token.Empty, Token.Empty)


        private val emptyTokenSerialized = Json.encodeToString(Token.Empty)
    }
}