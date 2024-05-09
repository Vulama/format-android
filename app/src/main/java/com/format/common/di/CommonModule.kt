package com.format.common.di

import com.format.BuildConfig
import com.format.common.infrastructure.configuration.Configuration
import com.format.common.infrastructure.configuration.DevConfiguration
import com.format.common.infrastructure.configuration.ProdConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val BACKGROUND_DISPATCHER_KEY = "backgroundDispatcher"
private const val UI_DISPATCHER_KEY = "uiDispatcher"
const val IO_DISPATCHER_KEY = "ioDispatcher"

val commonModule = module {
    // Configuration
    single<Configuration> {
        if (BuildConfig.FLAVOR == "prod") {
            ProdConfiguration
        } else {
            DevConfiguration
        }
    }

    // Threading
    single<CoroutineDispatcher>(named(BACKGROUND_DISPATCHER_KEY)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(UI_DISPATCHER_KEY)) { Dispatchers.Main }
    single<CoroutineDispatcher>(named(IO_DISPATCHER_KEY)) { Dispatchers.IO }
}

fun Scope.getBackgroundDispatcher() = get<CoroutineDispatcher>(named(BACKGROUND_DISPATCHER_KEY))
fun Scope.getUIDispatcher() = get<CoroutineDispatcher>(named(UI_DISPATCHER_KEY))
fun Scope.getIODispatcher() = get<CoroutineDispatcher>(named(IO_DISPATCHER_KEY))