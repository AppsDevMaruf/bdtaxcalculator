package com.maruf.bdtaxcalculator.play

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.edit
import androidx.core.net.toUri
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker

class InAppReviewManager(
    private val activity: ComponentActivity,
    private val reviewManager: ReviewManager = ReviewManagerFactory.create(activity)
) {
    private val preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun recordAppOpen() {
        preferences.edit {
            putInt(KEY_APP_OPEN_COUNT, preferences.getInt(KEY_APP_OPEN_COUNT, 0) + 1)
        }
    }

    fun requestAfterMeaningfulAction(trigger: String) {
        val actionCount = preferences.getInt(KEY_MEANINGFUL_ACTION_COUNT, 0) + 1
        preferences.edit { putInt(KEY_MEANINGFUL_ACTION_COUNT, actionCount) }

        val skipReason = reviewSkipReason(actionCount)
        if (skipReason != null) {
            FirebaseTracker.logEvent(
                "in_app_review_skipped",
                Bundle().apply {
                    putString("trigger", trigger)
                    putString("reason", skipReason)
                    putInt("meaningful_actions", actionCount)
                }
            )
            return
        }

        requestReview(trigger)
    }

    fun openStoreListing() {
        val packageName = activity.packageName
        val marketIntent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )

        try {
            activity.startActivity(marketIntent)
        } catch (_: ActivityNotFoundException) {
            activity.startActivity(webIntent)
        }
    }

    private fun requestReview(trigger: String) {
        val versionName = activity.currentVersionName()
        preferences.edit {
            putLong(KEY_LAST_ATTEMPT_AT, System.currentTimeMillis())
            putString(KEY_LAST_ATTEMPT_VERSION, versionName)
            putInt(KEY_ATTEMPT_COUNT, preferences.getInt(KEY_ATTEMPT_COUNT, 0) + 1)
        }

        FirebaseTracker.logEvent(
            "in_app_review_requested",
            Bundle().apply {
                putString("trigger", trigger)
                putString("version_name", versionName)
            }
        )

        reviewManager.requestReviewFlow()
            .addOnSuccessListener { reviewInfo ->
                reviewManager.launchReviewFlow(activity, reviewInfo)
                    .addOnCompleteListener { task ->
                        FirebaseTracker.logEvent(
                            "in_app_review_finished",
                            Bundle().apply {
                                putString("trigger", trigger)
                                putString("result", if (task.isSuccessful) "completed" else "failed")
                            }
                        )
                    }
                    .addOnFailureListener { throwable ->
                        FirebaseTracker.recordNonFatal(throwable)
                    }
            }
            .addOnFailureListener { throwable ->
                FirebaseTracker.logEvent(
                    "in_app_review_request_failed",
                    Bundle().apply { putString("trigger", trigger) }
                )
                FirebaseTracker.recordNonFatal(throwable)
            }
    }

    private fun reviewSkipReason(actionCount: Int): String? {
        val appOpenCount = preferences.getInt(KEY_APP_OPEN_COUNT, 0)
        val attemptCount = preferences.getInt(KEY_ATTEMPT_COUNT, 0)
        val lastAttemptAt = preferences.getLong(KEY_LAST_ATTEMPT_AT, 0L)
        val lastAttemptVersion = preferences.getString(KEY_LAST_ATTEMPT_VERSION, null)
        val currentVersion = activity.currentVersionName()
        val now = System.currentTimeMillis()

        return when {
            appOpenCount < MIN_APP_OPEN_COUNT -> "not_enough_app_opens"
            actionCount < MIN_MEANINGFUL_ACTION_COUNT -> "not_enough_actions"
            lastAttemptVersion == currentVersion -> "already_requested_this_version"
            attemptCount >= MAX_REVIEW_ATTEMPTS -> "attempt_limit_reached"
            now - lastAttemptAt < REVIEW_COOLDOWN_MS -> "cooldown_active"
            else -> null
        }
    }

    private fun Activity.currentVersionName(): String {
        return runCatching {
            packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown"
        }.getOrDefault("unknown")
    }

    private companion object {
        const val PREFERENCES_NAME = "in_app_review_preferences"
        const val KEY_APP_OPEN_COUNT = "app_open_count"
        const val KEY_MEANINGFUL_ACTION_COUNT = "meaningful_action_count"
        const val KEY_ATTEMPT_COUNT = "attempt_count"
        const val KEY_LAST_ATTEMPT_AT = "last_attempt_at"
        const val KEY_LAST_ATTEMPT_VERSION = "last_attempt_version"
        const val MIN_APP_OPEN_COUNT = 2
        const val MIN_MEANINGFUL_ACTION_COUNT = 3
        const val MAX_REVIEW_ATTEMPTS = 3
        const val REVIEW_COOLDOWN_MS = 14L * 24L * 60L * 60L * 1000L
    }
}
