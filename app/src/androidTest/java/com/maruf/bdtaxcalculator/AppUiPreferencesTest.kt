package com.maruf.bdtaxcalculator

import androidx.test.platform.app.InstrumentationRegistry
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppUiPreferencesTest {
    @Test
    fun onboardingIsIncompleteByDefaultAndPersistsCompletion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("taxpro_ui_preferences", 0)

        preferences.edit().clear().commit()
        try {
            assertFalse(AppUiPreferences.hasCompletedOnboarding(context))

            AppUiPreferences.setOnboardingCompleted(context)

            assertTrue(AppUiPreferences.hasCompletedOnboarding(context))
        } finally {
            preferences.edit().clear().commit()
        }
    }
}
