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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.play.PlayStoreUpdateManager
import com.maruf.bdtaxcalculator.ui.screen.AppRootScreen
import com.maruf.bdtaxcalculator.ui.theme.BDTaxCalculatorTheme

class MainActivity : ComponentActivity() {
    private lateinit var playStoreUpdateManager: PlayStoreUpdateManager

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
        playStoreUpdateManager.register()
        playStoreUpdateManager.checkForUpdates()
        askNotificationPermission()
        refreshFcmToken()
        setContent {
            BDTaxCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRootScreen()
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

