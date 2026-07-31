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
        InvestmentInputData("gpf", "GPF-এ নিজস্ব জমা"),
        InvestmentInputData("recognized_pf", "স্বীকৃত ভবিষ্য তহবিল"),
        InvestmentInputData("benevolent_group_insurance", "কল্যাণ তহবিল / গোষ্ঠী বিমা"),
        InvestmentInputData("superannuation", "অনুমোদিত সুপারএনুয়েশন ফান্ড"),
        InvestmentInputData("universal_pension", "সর্বজনীন পেনশন স্কিম"),
        InvestmentInputData("insurance", "জীবন বিমা / ডেফার্ড অ্যানুইটি"),
        InvestmentInputData("dps", "DPS", maxEligibleAmount = 120_000L),
        InvestmentInputData("sanchaypatra", "সরকারি সিকিউরিটিজ / সঞ্চয়পত্র", maxEligibleAmount = 500_000L),
        InvestmentInputData("dse", "তালিকাভুক্ত শেয়ার / স্টক / ডিবেঞ্চার"),
        InvestmentInputData("mutual", "ইউনিট / মিউচুয়াল ফান্ড / ETF", maxEligibleAmount = 500_000L),
        InvestmentInputData("zakat", "যাকাত তহবিলে দান"),
        InvestmentInputData("charitable_hospital", "অনুমোদিত দাতব্য হাসপাতালে দান"),
        InvestmentInputData("disability_welfare", "প্রতিবন্ধী কল্যাণ প্রতিষ্ঠানে দান"),
        InvestmentInputData("benevolent_education", "অনুমোদিত জনকল্যাণমূলক / শিক্ষা প্রতিষ্ঠানে দান"),
        InvestmentInputData("liberation_war", "মুক্তিযুদ্ধ স্মৃতি সংরক্ষণ প্রতিষ্ঠানে অনুদান"),
        InvestmentInputData("sro_approved_donation", "অন্যান্য SRO-অনুমোদিত দান / অনুদান")
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
