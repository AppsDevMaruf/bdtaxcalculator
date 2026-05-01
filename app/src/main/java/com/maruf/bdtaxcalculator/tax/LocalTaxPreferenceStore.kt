package com.maruf.bdtaxcalculator.tax

import android.content.Context

object LocalTaxPreferenceStore {
    private const val preferencesName = "taxpro_local_preferences"
    private const val keyDefaultTaxpayerType = "default_taxpayer_type"
    private const val keyAssessmentType = "assessment_type"

    const val assessmentRegular = "regular"
    const val assessmentNew = "new_assessment"

    fun getDefaultTaxpayerType(context: Context): String {
        val storedValue = preferences(context).getString(keyDefaultTaxpayerType, null)
        return storedValue
            ?.takeIf { savedType -> TaxDefaults.taxpayerTypes.any { it.id == savedType } }
            ?: TaxDefaults.taxpayerTypes.first().id
    }

    fun setDefaultTaxpayerType(context: Context, taxpayerTypeId: String) {
        if (TaxDefaults.taxpayerTypes.none { it.id == taxpayerTypeId }) return
        preferences(context).edit().putString(keyDefaultTaxpayerType, taxpayerTypeId).apply()
    }

    fun getAssessmentType(context: Context): String {
        return preferences(context).getString(keyAssessmentType, assessmentRegular)
            ?.takeIf { it == assessmentRegular || it == assessmentNew }
            ?: assessmentRegular
    }

    fun setAssessmentType(context: Context, assessmentType: String) {
        if (assessmentType != assessmentRegular && assessmentType != assessmentNew) return
        preferences(context).edit().putString(keyAssessmentType, assessmentType).apply()
    }

    fun getMinimumTax(context: Context): Double {
        return if (getAssessmentType(context) == assessmentNew) {
            TaxDefaults.newAssessmentMinimumTax
        } else {
            TaxDefaults.minimumTax
        }
    }

    fun clear(context: Context) {
        preferences(context).edit().clear().apply()
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
}
