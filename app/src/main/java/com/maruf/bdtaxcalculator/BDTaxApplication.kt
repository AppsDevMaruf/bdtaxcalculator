package com.maruf.bdtaxcalculator

import android.app.Application
import com.google.firebase.FirebaseApp
import com.maruf.bdtaxcalculator.firebase.AppNotificationChannels
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.tiktok.TikTokEventsTracker

class BDTaxApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppNotificationChannels.createDefaultChannel(this)
        FirebaseTracker.initialize(this)
        TikTokEventsTracker.initialize(this)
        FirebaseTracker.logAppOpened()
    }
}
