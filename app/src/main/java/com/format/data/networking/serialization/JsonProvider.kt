package com.format.data.networking.serialization

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class JsonProvider {

    companion object {

        fun get(): Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

        fun asApplicationJsonConverterFactory(): Converter.Factory =
            get().asConverterFactory("application/json".toMediaType())
    }
}
