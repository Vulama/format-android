package com.format.common.infrastructure.analytics

import com.format.common.model.AnalyticsEvent
import com.format.common.model.AnalyticsScreen

interface AnalyticsService {

    fun trackEvent(event: AnalyticsEvent)

    fun trackScreen(screen: AnalyticsScreen)

    fun setUser(userId: String)
}