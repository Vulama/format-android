package com.format.domain.user.repository

import arrow.core.Either
import com.format.common.model.AppError

interface UserRepository {

    suspend fun login(username: String, password: String): Either<AppError, Unit>
}