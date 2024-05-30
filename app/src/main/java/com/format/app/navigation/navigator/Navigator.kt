package com.format.app.navigation.navigator

import com.format.app.navigation.controller.NavHostControllerProvider

interface Navigator {

    fun navigate(route: String, clearStack: Boolean = false)

    fun goBack()

    class Default(
        private val navHostControllerProvider: NavHostControllerProvider,
    ) : Navigator {
        override fun navigate(route: String, clearStack: Boolean) {
            if (clearStack) {
                while (navHostControllerProvider.popBackStack()) {}
            }
            navHostControllerProvider.navigate(route)
        }

        override fun goBack() {
            navHostControllerProvider.popBackStack()
        }
    }
}