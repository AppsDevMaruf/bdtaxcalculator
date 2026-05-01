package com.maruf.bdtaxcalculator

import android.app.Application
import com.google.firebase.FirebaseApp
import com.maruf.bdtaxcalculator.firebase.AppNotificationChannels
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker

class BDTaxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppNotificationChannels.createDefaultChannel(this)
        FirebaseTracker.initialize(this)
        FirebaseTracker.logAppOpened()
    }
}
