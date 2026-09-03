package com.maruf.bdtaxcalculator.tiktok

import android.content.Context
import android.os.Bundle
import com.maruf.bdtaxcalculator.BuildConfig

object TikTokEventsTracker {
    private const val PARAM_TEST_EVENT_CODE = "test_event_code"

    private var isInitialized = false

    fun initialize(context: Context) {
        val appId = BuildConfig.TIKTOK_APP_ID
        val ttAppId = BuildConfig.TIKTOK_TT_APP_ID
        val appSecret = BuildConfig.TIKTOK_APP_SECRET
        if (appId.isBlank() || ttAppId.isBlank()) return

        runCatching {
            val sdkClass = Class.forName("com.tiktok.TikTokBusinessSdk")
            val configClass = findClass(
                "com.tiktok.TTConfig",
                "com.tiktok.TikTokBusinessSdk\$TTConfig"
            )
            val appContext = context.applicationContext
            val config = if (appSecret.isBlank()) {
                configClass.getConstructor(Context::class.java).newInstance(appContext)
            } else {
                configClass.getConstructor(Context::class.java, String::class.java)
                    .newInstance(appContext, appSecret)
            } ?: error("TikTok config was not created")

            config.callStringSetter("setAppId", appId)
            config.callStringSetter("setTTAppId", ttAppId)
            if (BuildConfig.TIKTOK_TEST_EVENT_CODE.isNotBlank()) {
                config.callNoArgMethod("openDebugMode")
            }

            sdkClass.getMethod("initializeSdk", configClass).invoke(null, config)
            sdkClass.methods.firstOrNull { it.name == "startTrack" && it.parameterTypes.isEmpty() }
                ?.invoke(null)
            isInitialized = true
        }
    }

    @Suppress("DEPRECATION")
    fun logEvent(name: String, params: Bundle? = null) {
        if (!isInitialized) return

        runCatching {
            val eventClass = Class.forName("com.tiktok.appevents.base.TTBaseEvent")
            val builder = eventClass.getMethod("newBuilder", String::class.java).invoke(null, name)
                ?: error("TikTok event builder was not created")
            if (BuildConfig.TIKTOK_TEST_EVENT_CODE.isNotBlank()) {
                builder.callObjectSetter("addProperty", PARAM_TEST_EVENT_CODE, BuildConfig.TIKTOK_TEST_EVENT_CODE)
            }
            params?.keySet()?.forEach { key ->
                params.get(key)?.let { value ->
                    builder.callObjectSetter("addProperty", key, value)
                }
            }
            val event = builder.javaClass.getMethod("build").invoke(builder)
            Class.forName("com.tiktok.TikTokBusinessSdk")
                .methods
                .firstOrNull { method ->
                    method.name == "trackTTEvent" &&
                        method.parameterTypes.size == 1 &&
                        method.parameterTypes.first().isAssignableFrom(eventClass)
                }
                ?.invoke(null, event)
        }
    }

    private fun findClass(vararg names: String): Class<*> {
        names.forEach { name ->
            runCatching { return Class.forName(name) }
        }
        error("TikTok TTConfig class was not found")
    }

    private fun Any.callStringSetter(name: String, value: String) {
        javaClass.methods
            .firstOrNull { method ->
                method.name == name &&
                    method.parameterTypes.size == 1 &&
                    method.parameterTypes.first() == String::class.java
            }
            ?.invoke(this, value)
    }

    private fun Any.callNoArgMethod(name: String) {
        javaClass.methods
            .firstOrNull { method -> method.name == name && method.parameterTypes.isEmpty() }
            ?.invoke(this)
    }

    private fun Any.callObjectSetter(name: String, key: String, value: Any) {
        javaClass.methods
            .firstOrNull { method ->
                method.name == name &&
                    method.parameterTypes.size == 2 &&
                    method.parameterTypes.first() == String::class.java
            }
            ?.invoke(this, key, value)
    }

}
