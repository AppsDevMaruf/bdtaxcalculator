package com.maruf.bdtaxcalculator.firebase

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.maruf.bdtaxcalculator.BuildConfig
import com.maruf.bdtaxcalculator.tiktok.TikTokEventsTracker

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
        crashlytics.apply {
            setCustomKey("app_build_type", BuildConfig.BUILD_TYPE)
            setCustomKey("android_sdk", Build.VERSION.SDK_INT)
            setCustomKey("device_manufacturer", Build.MANUFACTURER)
            setCustomKey("device_model", Build.MODEL)
            log("Firebase tracker initialized")
        }
    }

    fun logAppOpened() {
        crashlytics.log("App opened")
        analytics?.logEvent(FirebaseAnalytics.Event.APP_OPEN, null)
        TikTokEventsTracker.logEvent("app_open")
    }

    fun logScreen(screenName: String) {
        crashlytics.setCustomKey("current_screen", screenName)
        crashlytics.log("Screen: $screenName")
        analytics?.logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
            }
        )
    }

    fun logEvent(name: String, params: Bundle? = null) {
        crashlytics.log("Event: $name")
        analytics?.logEvent(name, params)
        TikTokEventsTracker.logEvent(name, params)
    }

    fun syncPreferenceProperties(
        language: String,
        themeMode: String,
        themePalette: String
    ) {
        crashlytics.setCustomKey(USER_PROPERTY_LANGUAGE, language)
        crashlytics.setCustomKey(USER_PROPERTY_THEME_MODE, themeMode)
        crashlytics.setCustomKey(USER_PROPERTY_DARK_PALETTE, themePalette)
        setUserProperty(USER_PROPERTY_LANGUAGE, language)
        setUserProperty(USER_PROPERTY_THEME_MODE, themeMode)
        setUserProperty(USER_PROPERTY_DARK_PALETTE, themePalette)
    }

    fun logLanguageChanged(language: String) {
        crashlytics.setCustomKey(USER_PROPERTY_LANGUAGE, language)
        crashlytics.log("Language changed")
        analytics?.logEvent(
            "app_language_changed",
            Bundle().apply { putString(PARAM_LANGUAGE, language) }
        )
        setUserProperty(USER_PROPERTY_LANGUAGE, language)
    }

    fun logThemeModeChanged(themeMode: String, themePalette: String) {
        crashlytics.setCustomKey(USER_PROPERTY_THEME_MODE, themeMode)
        crashlytics.setCustomKey(USER_PROPERTY_DARK_PALETTE, themePalette)
        crashlytics.log("Theme mode changed")
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
        crashlytics.setCustomKey(USER_PROPERTY_DARK_PALETTE, themePalette)
        crashlytics.log("Dark theme palette changed")
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
        crashlytics.log("FCM token refreshed")
        analytics?.logEvent("fcm_token_refreshed", null)
    }

    fun setSdkInitialized(name: String, initialized: Boolean) {
        crashlytics.setCustomKey("${name}_sdk_initialized", initialized)
        crashlytics.log("$name SDK initialized: $initialized")
    }

    fun logDiagnostic(message: String) {
        crashlytics.log(message.take(200))
    }

    fun recordNonFatal(throwable: Throwable) {
        crashlytics.log("Non-fatal: ${throwable.javaClass.simpleName}")
        crashlytics.recordException(throwable)
    }
}
