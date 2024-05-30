package com.format.data.networking.util

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.Response

@Serializable
data class ErrorResponse(
    val message: String? = null,
    val error: String? = null
)

fun <T> Response<T>.parseError(): Throwable {
    val errorBody = this.errorBody()
    return if (errorBody != null) {
        try {
            val errorResponseSer = Json.decodeFromString<ErrorResponse>(errorBody.string())
            Throwable(errorResponseSer.error ?: (errorResponseSer.message ?: "Api Error"))
        } catch (ex: Exception) {
            Throwable("Api Error")
        }
    } else {
        Throwable("Api Error")
    }
}