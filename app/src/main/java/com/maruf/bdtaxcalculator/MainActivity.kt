package com.maruf.bdtaxcalculator

import android.Manifest
import android.content.res.Configuration
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.play.AppUpdatePromptState
import com.maruf.bdtaxcalculator.play.InAppReviewManager
import com.maruf.bdtaxcalculator.play.PlayStoreUpdateManager
import com.maruf.bdtaxcalculator.ui.AppLocaleManager
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import com.maruf.bdtaxcalculator.ui.LocalAppLanguage
import com.maruf.bdtaxcalculator.ui.screen.AppUpdatePromptDialog
import com.maruf.bdtaxcalculator.ui.screen.AppRootScreen
import com.maruf.bdtaxcalculator.ui.theme.BDTaxCalculatorTheme

class MainActivity : AppCompatActivity() {
    private lateinit var playStoreUpdateManager: PlayStoreUpdateManager
    private lateinit var inAppReviewManager: InAppReviewManager
    private var appUpdatePromptState by mutableStateOf<AppUpdatePromptState?>(null)

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
        super.onCreate(savedInstanceState)

        val savedLanguage = AppUiPreferences.getLanguage(this)
        val savedThemeMode = AppUiPreferences.getThemeMode(this)
        val savedThemePalette = AppUiPreferences.getThemePalette(this)
        applySystemBarStyle(savedThemeMode.toDarkTheme(isSystemInDarkMode()))

        playStoreUpdateManager = PlayStoreUpdateManager(
            activity = this,
            updateLauncher = playUpdateLauncher,
            onUpdatePrompt = { promptState -> appUpdatePromptState = promptState }
        )
        inAppReviewManager = InAppReviewManager(this)
        inAppReviewManager.recordAppOpen()
        playStoreUpdateManager.register()
        playStoreUpdateManager.checkForUpdates()
        askNotificationPermission()
        refreshFcmToken()

        AppLocaleManager.apply(savedLanguage)
        FirebaseTracker.syncPreferenceProperties(
            language = savedLanguage,
            themeMode = savedThemeMode,
            themePalette = savedThemePalette
        )

        setContent {
            var language by remember { mutableStateOf(savedLanguage) }
            var themeMode by remember { mutableStateOf(savedThemeMode) }
            var themePalette by remember { mutableStateOf(savedThemePalette) }
            val systemDark = isSystemInDarkTheme()
            val darkTheme = themeMode.toDarkTheme(systemDark)

            SideEffect {
                applySystemBarStyle(darkTheme)
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
                                if (language != newLanguage) {
                                    FirebaseTracker.logLanguageChanged(newLanguage)
                                }
                                AppUiPreferences.setLanguage(this, newLanguage)
                                AppLocaleManager.apply(newLanguage)
                                language = newLanguage
                            },
                            onThemeModeChange = { newThemeMode ->
                                if (themeMode != newThemeMode) {
                                    FirebaseTracker.logThemeModeChanged(
                                        themeMode = newThemeMode,
                                        themePalette = themePalette
                                    )
                                }
                                themeMode = newThemeMode
                                AppUiPreferences.setThemeMode(this, newThemeMode)
                            },
                            themePalette = themePalette,
                            onThemePaletteChange = { newThemePalette ->
                                if (themePalette != newThemePalette) {
                                    FirebaseTracker.logDarkThemePaletteSelected(newThemePalette)
                                }
                                themePalette = newThemePalette
                                AppUiPreferences.setThemePalette(this, newThemePalette)
                            },
                            onRequestInAppReview = inAppReviewManager::requestAfterMeaningfulAction,
                            onOpenStoreListing = inAppReviewManager::openStoreListing
                        )

                        appUpdatePromptState?.let { updatePrompt ->
                            AppUpdatePromptDialog(
                                state = updatePrompt,
                                onMaybeLater = playStoreUpdateManager::dismissPendingUpdatePrompt,
                                onUpdateNow = playStoreUpdateManager::startPendingUpdate
                            )
                        }
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

    private fun applySystemBarStyle(darkTheme: Boolean) {
        val transparent = android.graphics.Color.TRANSPARENT
        val barStyle = if (darkTheme) {
            SystemBarStyle.dark(transparent)
        } else {
            SystemBarStyle.light(transparent, transparent)
        }
        enableEdgeToEdge(
            statusBarStyle = barStyle,
            navigationBarStyle = barStyle
        )
    }

    private fun String.toDarkTheme(systemDark: Boolean): Boolean {
        return when (this) {
            AppUiPreferences.themeDark -> true
            AppUiPreferences.themeLight -> false
            else -> systemDark
        }
    }

    private fun isSystemInDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
            Configuration.UI_MODE_NIGHT_YES
    }

}
