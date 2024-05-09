package com.format.data.networking.token

import com.format.common.model.Epoch
import com.format.common.model.toEpoch
import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val accessToken: Token,
    val refreshToken: Token,
)

@Serializable
data class Token(
    val value: String,
    val expiresAt: Epoch,
) {
    companion object {

        val Empty = Token("", Epoch.None)

        fun of(value: String, expiresAt: String): Token {
            if (value.isEmpty() || expiresAt.isEmpty()) {
                return Empty
            }

            val timeStamp = expiresAt.toEpoch()
            return if (timeStamp != Epoch.None) {
                Token(
                    value = value,
                    expiresAt = timeStamp
                )
            } else {
                Empty
            }
        }
    }
}