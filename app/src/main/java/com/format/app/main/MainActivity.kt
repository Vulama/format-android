package com.format.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.format.NavGraphs.root
import com.format.app.navigation.controller.NavHostControllerProvider
import com.format.app.theme.ColorPalette
import com.format.app.theme.ForMatTheme
import com.format.common.infrastructure.analytics.AnalyticsService
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import org.koin.android.ext.android.inject
import android.provider.Settings
import com.format.common.model.AnalyticsEvent
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class MainActivity : ComponentActivity() {

    private val navHostControllerProvider: NavHostControllerProvider by inject()

    private val analyticsService: AnalyticsService by inject()

    @SuppressLint("HardwareIds")
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
        analyticsService.setUser(deviceId)
        analyticsService.trackEvent(AnalyticsEvent.ApplicationStarted)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            navHostControllerProvider.inject(navController)

            ForMatTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DestinationsNavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(color = ColorPalette.Surface),
                        navGraph = root,
                        navController = navController,
                        engine = rememberAnimatedNavHostEngine(
                            rootDefaultAnimations = RootNavGraphDefaultAnimations(
                                enterTransition = { fadeIn(tween(200, 100)) },
                                exitTransition = { fadeOut(tween(200)) },
                            )
                        ),
                    )
                }
            }
        }
    }
}