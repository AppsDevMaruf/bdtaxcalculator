package com.maruf.bdtaxcalculator.tax

import android.content.Context

object LocalTaxPreferenceStore {
    private const val preferencesName = "taxpro_local_preferences"
    private const val keyDefaultTaxpayerType = "default_taxpayer_type"
    private const val keyAssessmentType = "assessment_type"
    private const val keyIncomeYear = "income_year"
    private const val keyTaxpayerLocation = "taxpayer_location"

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

    fun getIncomeYear(context: Context): String {
        val storedValue = preferences(context).getString(keyIncomeYear, null)
        return TaxYearCatalog.find(storedValue.orEmpty()).incomeYear
    }

    fun setIncomeYear(context: Context, incomeYear: String) {
        if (TaxYearCatalog.supportedYears.none { it.incomeYear == incomeYear }) return
        preferences(context).edit().putString(keyIncomeYear, incomeYear).apply()
    }

    fun getTaxpayerLocation(context: Context): TaxpayerLocation {
        val storedValue = preferences(context).getString(keyTaxpayerLocation, null)
        return TaxpayerLocation.entries.firstOrNull { it.id == storedValue }
            ?: TaxpayerLocation.DhakaOrChattogramCity
    }

    fun setTaxpayerLocation(context: Context, location: TaxpayerLocation) {
        preferences(context).edit().putString(keyTaxpayerLocation, location.id).apply()
    }

    fun clear(context: Context) {
        preferences(context).edit().clear().apply()
    }

    private fun preferences(context: Context) =
        context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
}
