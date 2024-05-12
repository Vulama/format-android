package com.format.domain.user.repository

import arrow.core.Either
import com.format.common.model.AppError
import com.format.domain.model.User

interface UserRepository {

    suspend fun login(username: String, passwordHash: String): Either<AppError, Unit>

    suspend fun register(username: String, passwordHash: String): Either<AppError, User>
}