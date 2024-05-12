package com.format.domain.model

import kotlinx.serialization.SerialName

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
)