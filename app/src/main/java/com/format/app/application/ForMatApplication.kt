package com.format.app.application

import android.app.Application
import com.format.app.di.initDI
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.android.getKoin
import kotlin.coroutines.CoroutineContext

class ForMatApplication : Application(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob()

    override fun onCreate() {
        super.onCreate()

        // Dependency injection initialisation
        initDI()

        // Firebase initialisation
        Firebase.initialize(this)
    }
}