package com.maruf.bdtaxcalculator.play

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker

enum class AppUpdatePromptType {
    Soft,
    Force
}

data class AppUpdatePromptState(
    val type: AppUpdatePromptType,
    val currentVersionName: String,
    val availableVersionCode: Int,
    val priority: Int,
    val stalenessDays: Int?
) {
    val isForce: Boolean = type == AppUpdatePromptType.Force
}

class PlayStoreUpdateManager(
    private val activity: ComponentActivity,
    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val onUpdatePrompt: (AppUpdatePromptState?) -> Unit = {}
) {
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
    private var activeUpdateType: Int? = null
    private var pendingUpdateInfo: AppUpdateInfo? = null
    private var pendingUpdateType: Int? = null

    private val installStateListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                FirebaseTracker.logEvent(
                    "play_update_downloading",
                    Bundle().apply {
                        putLong("downloaded_bytes", state.bytesDownloaded())
                        putLong("total_bytes", state.totalBytesToDownload())
                    }
                )
            }

            InstallStatus.DOWNLOADED -> completeFlexibleUpdate()
            InstallStatus.FAILED -> {
                FirebaseTracker.logEvent(
                    "play_update_failed",
                    Bundle().apply { putInt("error_code", state.installErrorCode()) }
                )
            }
        }
    }

    fun register() {
        appUpdateManager.registerListener(installStateListener)
    }

    fun unregister() {
        appUpdateManager.unregisterListener(installStateListener)
    }

    fun checkForUpdates() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener(::handleUpdateInfo)
            .addOnFailureListener {
                FirebaseTracker.logEvent("play_update_check_failed")
            }
    }

    fun resumeUpdateIfNeeded() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                when {
                    appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                        clearPendingUpdatePrompt()
                        startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE)
                    }

                    appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED -> {
                        completeFlexibleUpdate()
                    }
                }
            }
    }

    fun onUpdateFlowResult(resultCode: Int) {
        val updateType = activeUpdateType
        val resultName = when (resultCode) {
            Activity.RESULT_OK -> "accepted"
            Activity.RESULT_CANCELED -> "cancelled"
            else -> "failed"
        }

        FirebaseTracker.logEvent(
            "play_update_flow_result",
            Bundle().apply {
                putString("result", resultName)
                putString("update_type", updateType?.toUpdateTypeName())
            }
        )

        if (updateType == AppUpdateType.IMMEDIATE && resultCode != Activity.RESULT_OK) {
            checkForUpdates()
        }
    }

    fun startPendingUpdate() {
        val appUpdateInfo = pendingUpdateInfo ?: return
        val updateType = pendingUpdateType ?: return
        clearPendingUpdatePrompt()
        startUpdate(appUpdateInfo, updateType)
    }

    fun dismissPendingUpdatePrompt() {
        val updateType = pendingUpdateType
        if (updateType == AppUpdateType.IMMEDIATE) return

        FirebaseTracker.logEvent(
            "play_update_prompt_dismissed",
            Bundle().apply { putString("update_type", updateType?.toUpdateTypeName()) }
        )
        clearPendingUpdatePrompt()
    }

    private fun handleUpdateInfo(appUpdateInfo: AppUpdateInfo) {
        if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) return

        val updateType = chooseUpdateType(appUpdateInfo) ?: return
        pendingUpdateInfo = appUpdateInfo
        pendingUpdateType = updateType

        val promptState = AppUpdatePromptState(
            type = if (updateType == AppUpdateType.IMMEDIATE) AppUpdatePromptType.Force else AppUpdatePromptType.Soft,
            currentVersionName = activity.currentVersionName(),
            availableVersionCode = appUpdateInfo.availableVersionCode(),
            priority = appUpdateInfo.updatePriority(),
            stalenessDays = appUpdateInfo.clientVersionStalenessDays()
        )

        FirebaseTracker.logEvent(
            "play_update_prompt_shown",
            Bundle().apply {
                putString("update_type", updateType.toUpdateTypeName())
                putInt("available_version_code", appUpdateInfo.availableVersionCode())
                putInt("priority", appUpdateInfo.updatePriority())
                putInt("staleness_days", appUpdateInfo.clientVersionStalenessDays() ?: -1)
            }
        )
        notifyUpdatePrompt(promptState)
    }

    private fun chooseUpdateType(appUpdateInfo: AppUpdateInfo): Int? {
        val isForceUpdate = appUpdateInfo.updatePriority() >= FORCE_UPDATE_PRIORITY

        return when {
            isForceUpdate && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                AppUpdateType.IMMEDIATE
            }

            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                AppUpdateType.FLEXIBLE
            }

            else -> null
        }
    }

    private fun startUpdate(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        activeUpdateType = updateType
        FirebaseTracker.logEvent(
            "play_update_started",
            Bundle().apply {
                putString("update_type", updateType.toUpdateTypeName())
                putInt("priority", appUpdateInfo.updatePriority())
                putInt("staleness_days", appUpdateInfo.clientVersionStalenessDays() ?: -1)
            }
        )

        runCatching {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.newBuilder(updateType).build()
            )
        }.onFailure(FirebaseTracker::recordNonFatal)
    }

    private fun clearPendingUpdatePrompt() {
        pendingUpdateInfo = null
        pendingUpdateType = null
        notifyUpdatePrompt(null)
    }

    private fun notifyUpdatePrompt(state: AppUpdatePromptState?) {
        activity.runOnUiThread { onUpdatePrompt(state) }
    }

    private fun completeFlexibleUpdate() {
        Toast.makeText(activity, "Update downloaded. Restarting app...", Toast.LENGTH_LONG).show()
        appUpdateManager.completeUpdate()
    }

    private fun Int.toUpdateTypeName(): String {
        return when (this) {
            AppUpdateType.IMMEDIATE -> "force"
            AppUpdateType.FLEXIBLE -> "soft"
            else -> "unknown"
        }
    }

    private companion object {
        const val FORCE_UPDATE_PRIORITY = 4
    }
}

private fun Activity.currentVersionName(): String {
    return runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "unknown"
    }.getOrDefault("unknown")
}
