package com.format.common.model

sealed interface AppError {
    val message: String

    data class ApiError(override val message: String) : AppError
}