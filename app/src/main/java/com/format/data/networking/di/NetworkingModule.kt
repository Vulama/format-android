package com.format.data.networking.di

import android.content.SharedPreferences
import com.format.common.di.getIODispatcher
import com.format.common.infrastructure.configuration.Configuration
import com.format.common.infrastructure.logger.Logger
import com.format.data.api.PublicApi
import com.format.data.infrastructure.dateTime.DateTimeProvider
import com.format.data.networking.interceptor.AuthorizationInterceptor
import com.format.data.networking.serialization.JsonProvider
import com.format.data.networking.serialization.JsonProvider.Companion.asApplicationJsonConverterFactory
import com.format.data.networking.token.TokenStore
import com.format.data.networking.util.TokenRefresher
import com.format.data.networking.util.TokenValidityChecker
import kotlinx.serialization.json.Json
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import java.time.Duration
import java.util.concurrent.TimeUnit

private val HTTP_CLIENT_TIMEOUT = Duration.ofSeconds(60)
private const val MAX_COUNT_IDLE_CONNECTIONS = 5
private const val IDLE_CONNECTION_LIFETIME = 1L

private const val GENERAL_OK_HTTP_CLIENT_KEY = "general-ok-http-client"
private const val AUTH_OK_HTTP_CLIENT_KEY = "auth-ok-http-client"

private const val GENERAL_RETROFIT_KEY = "general-retrofit-client"
private const val AUTH_RETROFIT_KEY = "auth-retrofit-client"

fun Scope.getGeneralRetrofit() = get<Retrofit>(named(GENERAL_RETROFIT_KEY))
fun Scope.getAuthRetrofit() = get<Retrofit>(named(AUTH_RETROFIT_KEY))

val networkingModule = module {
    single<Retrofit>(named(GENERAL_RETROFIT_KEY)) {
        get<Retrofit.Builder>()
            .baseUrl(get<Configuration>().apiBaseUrl)
            .client(get<OkHttpClient>(named(GENERAL_OK_HTTP_CLIENT_KEY)))
            .build()
    }

    single<Retrofit>(named(AUTH_RETROFIT_KEY)) {
        get<Retrofit.Builder>()
            .baseUrl(get<Configuration>().apiBaseUrl)
            .client(get<OkHttpClient>(named(AUTH_OK_HTTP_CLIENT_KEY)))
            .build()
    }

    single<Retrofit.Builder> {
        Retrofit.Builder()
            .addConverterFactory(asApplicationJsonConverterFactory())
    }

    single<Json> { JsonProvider.get() }

    single<AuthorizationInterceptor> {
        AuthorizationInterceptor(
            tokenStore = get<TokenStore>(),
            tokenValidityChecker = get<TokenValidityChecker>(),
            tokenRefresher = get<TokenRefresher>(),
            logger = get<Logger>(),
        )
    }

    single<OkHttpClient>(named(GENERAL_OK_HTTP_CLIENT_KEY)) {
        val builder = OkHttpClient.Builder()
            .connectionPool(get())

        builder
            .connectTimeout(HTTP_CLIENT_TIMEOUT)
            .callTimeout(HTTP_CLIENT_TIMEOUT)
            .readTimeout(HTTP_CLIENT_TIMEOUT)
            .writeTimeout(HTTP_CLIENT_TIMEOUT)

        builder.build()
    }

    single<OkHttpClient>(named(AUTH_OK_HTTP_CLIENT_KEY)) {
        val builder = OkHttpClient.Builder()
            .connectionPool(get())

        builder
            .connectTimeout(HTTP_CLIENT_TIMEOUT)
            .callTimeout(HTTP_CLIENT_TIMEOUT)
            .readTimeout(HTTP_CLIENT_TIMEOUT)
            .writeTimeout(HTTP_CLIENT_TIMEOUT)

        builder.addInterceptor(get<AuthorizationInterceptor>())
        builder.build()
    }

    single<ConnectionPool> {
        ConnectionPool(MAX_COUNT_IDLE_CONNECTIONS, IDLE_CONNECTION_LIFETIME, TimeUnit.MINUTES)
    }

    single<TokenStore> {
        TokenStore.Default(
            get<SharedPreferences>(),
        )
    }

    single<TokenRefresher> {
        TokenRefresher.Default(
            getIODispatcher(),
            get<TokenStore>(),
            get<PublicApi>(),
        )
    }

    single<TokenValidityChecker> {
        TokenValidityChecker.Default(
            get<DateTimeProvider>(),
        )
    }
}