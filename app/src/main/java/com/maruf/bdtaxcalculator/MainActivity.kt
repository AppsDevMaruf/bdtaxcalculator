package com.maruf.bdtaxcalculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.play.InAppReviewManager
import com.maruf.bdtaxcalculator.play.PlayStoreUpdateManager
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import com.maruf.bdtaxcalculator.ui.LocalAppLanguage
import com.maruf.bdtaxcalculator.ui.screen.AppRootScreen
import com.maruf.bdtaxcalculator.ui.theme.BDTaxCalculatorTheme

class MainActivity : ComponentActivity() {
    private lateinit var playStoreUpdateManager: PlayStoreUpdateManager
    private lateinit var inAppReviewManager: InAppReviewManager

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        FirebaseTracker.logEvent(
            if (granted) "notification_permission_granted" else "notification_permission_denied"
        )
    }

    private val playUpdateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        playStoreUpdateManager.onUpdateFlowResult(result.resultCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        playStoreUpdateManager = PlayStoreUpdateManager(this, playUpdateLauncher)
        inAppReviewManager = InAppReviewManager(this)
        inAppReviewManager.recordAppOpen()
        playStoreUpdateManager.register()
        playStoreUpdateManager.checkForUpdates()
        askNotificationPermission()
        refreshFcmToken()
        setContent {
            var language by remember { mutableStateOf(AppUiPreferences.getLanguage(this)) }
            var themeMode by remember { mutableStateOf(AppUiPreferences.getThemeMode(this)) }
            var themePalette by remember { mutableStateOf(AppUiPreferences.getThemePalette(this)) }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                AppUiPreferences.themeDark -> true
                AppUiPreferences.themeLight -> false
                else -> systemDark
            }

            CompositionLocalProvider(LocalAppLanguage provides language) {
                BDTaxCalculatorTheme(
                    darkTheme = darkTheme,
                    themePalette = themePalette
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppRootScreen(
                            language = language,
                            themeMode = themeMode,
                            onLanguageChange = { newLanguage ->
                                language = newLanguage
                                AppUiPreferences.setLanguage(this, newLanguage)
                            },
                            onThemeModeChange = { newThemeMode ->
                                themeMode = newThemeMode
                                AppUiPreferences.setThemeMode(this, newThemeMode)
                            },
                            themePalette = themePalette,
                            onThemePaletteChange = { newThemePalette ->
                                themePalette = newThemePalette
                                AppUiPreferences.setThemePalette(this, newThemePalette)
                            },
                            onRequestInAppReview = inAppReviewManager::requestAfterMeaningfulAction,
                            onOpenStoreListing = inAppReviewManager::openStoreListing
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playStoreUpdateManager.resumeUpdateIfNeeded()
    }

    override fun onDestroy() {
        playStoreUpdateManager.unregister()
        super.onDestroy()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    private fun refreshFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                FirebaseTracker.setFcmToken(token)
                android.util.Log.d("FCM_TOKEN", token)
            }
            .addOnFailureListener(FirebaseTracker::recordNonFatal)
    }


}
