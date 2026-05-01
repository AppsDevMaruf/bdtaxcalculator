package com.maruf.bdtaxcalculator.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object FirebaseTracker {
    private val crashlytics: FirebaseCrashlytics
        get() = FirebaseCrashlytics.getInstance()

    private var analytics: FirebaseAnalytics? = null

    fun initialize(context: Context) {
        analytics = FirebaseAnalytics.getInstance(context.applicationContext)
    }

    fun logAppOpened() {
        analytics?.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
    }

    fun logScreen(screenName: String) {
        analytics?.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
        )
    }

    fun logEvent(name: String, params: Bundle? = null) {
        analytics?.logEvent(name, params)
    }

    fun setUserProperty(name: String, value: String?) {
        analytics?.setUserProperty(name, value)
    }

    fun setFcmToken(token: String) {
        crashlytics.setCustomKey("fcm_token_available", token.isNotBlank())
        analytics?.logEvent("fcm_token_refreshed", null)
    }

    fun recordNonFatal(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
