package com.format.welcome.ui

import com.format.welcome.ui.destinations.*
import com.ramcosta.composedestinations.spec.*

/**
 * Class generated if any Composable is annotated with `@Destination`.
 * It aggregates all [TypedDestination]s in their [NavGraph]s.
 */
public object NavGraphs {

    public val root: NavGraph = NavGraph(
        route = "root",
        startRoute = WelcomeScreenDestination,
        destinations = listOf(
            LoginScreenDestination,
			RegisterScreenDestination,
			WelcomeScreenDestination
        )
    )
}