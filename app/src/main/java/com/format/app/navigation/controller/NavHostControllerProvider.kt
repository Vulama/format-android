package com.format.app.navigation.controller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.format.domain.infrastructure.Logger
import com.format.domain.model.ApplicationFlows
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

interface NavHostControllerProvider {

    @OptIn(ExperimentalMaterialNavigationApi::class)
    fun inject(navHostController: NavHostController)

    fun dispose()

    fun navigate(route: String)

    fun navigateExternal(createIntent: Context.() -> Intent, destinationDescription: String): Boolean

    fun popBackStack(): Boolean

    fun popBackStackTo(route: String, inclusive: Boolean): Boolean

    fun currentRoute(): String?

    fun remove(removeList: List<String>)

    val context: Context?

    class Default(private val logger: Logger) : NavHostControllerProvider {

        private var _navHostController: NavHostController? = null

        @OptIn(ExperimentalMaterialNavigationApi::class)
        override fun inject(navHostController: NavHostController) {
            _navHostController = navHostController

            _navHostController?.addOnDestinationChangedListener { _, _, arguments ->
                logDestinationChange(arguments)
            }
        }

        private fun logDestinationChange(arguments: Bundle?) {
            val args = try {
                with(Uri.parse(currentRoute())) {
                    queryParameterNames.map {
                        val argName = getQueryParameter(it)?.removeSurrounding("{", "}")
                        "$argName: ${arguments?.getString(argName)}"
                    }
                }
            } catch (ex: Exception) {
                listOf("Error parsing args: ${ex.message}")
            }

            logger.i(ApplicationFlows.navigation, "Navigated to: ${currentRoute()}, args:$args")
        }

        override fun dispose() {
            _navHostController = null
        }

        override fun navigate(route: String) {
            try {
                _navHostController?.navigate(route)
            } catch (ex: Exception) {
                logger.w(ApplicationFlows.navigation, "There was an error navigating: $ex")
            }
        }

        override fun popBackStack(): Boolean {
            return _navHostController?.popBackStack() ?: false
        }

        override fun popBackStackTo(route: String, inclusive: Boolean): Boolean {
            return _navHostController?.popBackStack(route, inclusive, false) ?: false
        }

        override fun navigateExternal(createIntent: Context.() -> Intent, destinationDescription: String): Boolean {
            return context?.let { ctx ->
                try {
                    ContextCompat.startActivity(ctx, ctx.createIntent(), null)
                    logger.i(ApplicationFlows.navigation, "Navigated externally to: $destinationDescription")
                    true
                } catch (ex: Exception) {
                    logger.e(t = ex, tag = ApplicationFlows.navigation, message = "External navigation to $destinationDescription failed, reason: $destinationDescription")
                    false
                }
            } ?: false
        }

        override fun currentRoute(): String? =
            _navHostController?.currentBackStackEntry?.destination?.route

        override fun remove(removeList: List<String>) {
            try {
                while (removeList.any { it == currentRoute() }) {
                    _navHostController?.popBackStack()
                }
            } catch (ex: Exception) {
                logger.i(ApplicationFlows.navigation, "Error when removing from navStack: $ex")
            }
        }

        override val context: Context?
            get() = _navHostController?.context
    }
}