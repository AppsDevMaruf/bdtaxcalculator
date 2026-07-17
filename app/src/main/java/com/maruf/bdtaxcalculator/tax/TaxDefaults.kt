package com.maruf.bdtaxcalculator.tax

object TaxDefaults {
    const val incomeYearLabel = "২০২৫-২৬"
    const val assessmentYearLabel = "২০২৬-২৭"
    const val taxYearLabel = assessmentYearLabel
    const val maxTotalExemption = 500_000L
    const val minimumTax = 5_000.0
    const val newAssessmentMinimumTax = 1_000.0
    const val maxInvestmentRebate = 750_000.0
    const val incomeBasedInvestmentRebateRate = 0.03
    const val investmentRebateRate = 0.10
    const val disabledDependentAllowance = 50_000L

    val taxpayerTypes get() = TaxYearCatalog.current.taxpayerTypes

    val investmentOptions = listOf(
        InvestmentInputData("dse", "DSE শেয়ার"),
        InvestmentInputData("sanchaypatra", "সঞ্চয়পত্র"),
        InvestmentInputData("dps", "DPS (ডিপোজিট পেনশন স্কিম)"),
        InvestmentInputData("mutual", "মিউচুয়াল ফান্ড"),
        InvestmentInputData("insurance", "লাইফ ইন্স্যুরেন্স")
    )

    val taxSlabs = listOf(
        "প্রথম করমুক্ত সীমা পর্যন্ত" to "শূন্য",
        "পরবর্তী ৩,০০,০০০ টাকা" to "১০%",
        "পরবর্তী ৪,০০,০০০ টাকা" to "১৫%",
        "পরবর্তী ৫,০০,০০০ টাকা" to "২০%",
        "পরবর্তী ২০,০০,০০০ টাকা" to "২৫%",
        "অবশিষ্ট আয়" to "৩০%"
    )
}
