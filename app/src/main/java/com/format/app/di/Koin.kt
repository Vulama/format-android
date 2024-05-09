package com.format.app.di

import android.app.Application
import com.format.app.navigation.di.navigationModule
import com.format.common.di.commonModule
import com.format.data.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun Application.initDI() {
    startKoin {
        androidLogger()
        androidContext(this@initDI)
        modules(
            listOf(
                dataModule,
                navigationModule,
                commonModule,
            )
        )
    }
}