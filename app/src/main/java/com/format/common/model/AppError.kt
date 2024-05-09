package com.format.common.model

sealed interface AppError {
    data object ApiError : AppError
}