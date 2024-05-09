package com.format.app.navigation.navigator

import com.format.app.navigation.controller.NavHostControllerProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Navigator {

    fun navigate(route: String)

    fun goBack()

    class Default(
        private val navHostControllerProvider: NavHostControllerProvider,
    ) : Navigator {
        override fun navigate(route: String) {
            navHostControllerProvider.navigate(route)
        }

        override fun goBack() {
            navHostControllerProvider.popBackStack()
        }
    }
}