package com.format.data.infrastructure.analytics

import android.os.Bundle
import com.format.common.infrastructure.analytics.AnalyticsService
import com.format.common.model.AnalyticsEvent
import com.format.common.model.AnalyticsScreen
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class FirebaseAnalytics : AnalyticsService {
    override fun trackEvent(event: AnalyticsEvent) {
        val params = Bundle().apply {
            event.parameters.forEach { (key, value) ->
                this.putString(key, value)
            }
        }
        Firebase.analytics.logEvent(event.name, params)
    }

    override fun trackScreen(screen: AnalyticsScreen) {
        Firebase.analytics.logEvent("screen_${screen.name}", null)
    }

    override fun setUser(userId: String) {
        Firebase.analytics.setUserId(userId)
    }
}