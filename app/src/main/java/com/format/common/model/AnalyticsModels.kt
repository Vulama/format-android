package com.format.common.model

sealed class AnalyticsEvent(val name: String, val parameters: Map<String, String> = emptyMap()) {
    data object ApplicationStarted : AnalyticsEvent("application_started")
    data class FormulaDownloaded(val groupId: Int) : AnalyticsEvent(
        name = "group_downloaded",
        parameters = mapOf("groupId" to groupId.toString())
    )
    data class FormulaDeleted(val groupId: Int) : AnalyticsEvent(
        name = "group_removed",
        parameters = mapOf("groupId" to groupId.toString())
    )
    data class FavouriteToggled(val groupId: Int, val isFavourite: Boolean) : AnalyticsEvent(
        name = "favourite_toggled",
        parameters = mapOf(
            "groupId" to groupId.toString(),
            "isFavourite" to isFavourite.toString(),
        ),
    )
}

sealed class AnalyticsScreen(val name: String) {
    data object WelcomeScreen : AnalyticsScreen("welcome")
    data object LoginScreen : AnalyticsScreen("login")
    data object RegisterScreen : AnalyticsScreen("register")
    data object HomeScreen: AnalyticsScreen("home")
    data object DownloadScreen : AnalyticsScreen("download")
    data object GroupPreviewScreen : AnalyticsScreen("group_preview")
    data object FormulaPreviewScreen : AnalyticsScreen("formula_preview")
    data object GroupEditScreen : AnalyticsScreen("group_edit")
}