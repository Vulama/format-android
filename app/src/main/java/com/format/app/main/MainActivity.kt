package com.format.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.format.app.navigation.controller.NavHostControllerProvider
import com.format.app.theme.ColorPalette
import com.format.app.theme.ForMatTheme
import com.format.welcome.ui.NavGraphs.root
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val navHostControllerProvider: NavHostControllerProvider by inject()

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                enterTransition = { EnterTransition.None },
                                exitTransition = { ExitTransition.None },
                            )
                        ),
                    )
                }
            }
        }
    }
}