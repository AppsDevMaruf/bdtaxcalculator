package com.maruf.bdtaxcalculator.firebase

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object FirebaseTracker {
    private const val PARAM_LANGUAGE = "language"
    private const val PARAM_THEME_MODE = "theme_mode"
    private const val PARAM_PALETTE = "palette"
    private const val PARAM_SERVICE = "service"
    private const val PARAM_ACTION = "action"
    private const val PARAM_TOTAL_COUNT = "total_count"
    private const val PARAM_UNREAD_COUNT = "unread_count"

    private const val USER_PROPERTY_LANGUAGE = "app_language"
    private const val USER_PROPERTY_THEME_MODE = "theme_mode"
    private const val USER_PROPERTY_DARK_PALETTE = "dark_theme_palette"

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

    fun syncPreferenceProperties(
        language: String,
        themeMode: String,
        themePalette: String
    ) {
        setUserProperty(USER_PROPERTY_LANGUAGE, language)
        setUserProperty(USER_PROPERTY_THEME_MODE, themeMode)
        setUserProperty(USER_PROPERTY_DARK_PALETTE, themePalette)
    }

    fun logLanguageChanged(language: String) {
        analytics?.logEvent(
            "app_language_changed",
            Bundle().apply { putString(PARAM_LANGUAGE, language) }
        )
        setUserProperty(USER_PROPERTY_LANGUAGE, language)
    }

    fun logThemeModeChanged(themeMode: String, themePalette: String) {
        analytics?.logEvent(
            "app_theme_mode_changed",
            Bundle().apply {
                putString(PARAM_THEME_MODE, themeMode)
                putString(PARAM_PALETTE, themePalette)
            }
        )
        setUserProperty(USER_PROPERTY_THEME_MODE, themeMode)
        setUserProperty(USER_PROPERTY_DARK_PALETTE, themePalette)
    }

    fun logDarkThemePaletteSelected(themePalette: String) {
        analytics?.logEvent(
            "dark_theme_palette_selected",
            Bundle().apply { putString(PARAM_PALETTE, themePalette) }
        )
        setUserProperty(USER_PROPERTY_DARK_PALETTE, themePalette)
    }

    fun logHomeServiceOpened(service: String) {
        analytics?.logEvent(
            "home_service_opened",
            Bundle().apply { putString(PARAM_SERVICE, service) }
        )
    }

    fun logNotificationInboxOpened(totalCount: Int, unreadCount: Int) {
        analytics?.logEvent(
            "notification_inbox_opened",
            Bundle().apply {
                putInt(PARAM_TOTAL_COUNT, totalCount)
                putInt(PARAM_UNREAD_COUNT, unreadCount)
            }
        )
    }

    fun logNotificationInboxCleared(totalCount: Int) {
        analytics?.logEvent(
            "notification_inbox_cleared",
            Bundle().apply { putInt(PARAM_TOTAL_COUNT, totalCount) }
        )
    }

    fun logSettingsAction(action: String) {
        analytics?.logEvent(
            "settings_action_clicked",
            Bundle().apply { putString(PARAM_ACTION, action) }
        )
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
