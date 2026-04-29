package com.maruf.bdtaxcalculator.tax

object TaxDefaults {
    const val taxYearLabel = "২০২৫-২৬"
    const val maxHouseRentExemption = 300_000L
    const val maxMedicalExemption = 120_000L
    const val maxConveyanceExemption = 30_000L
    const val maxTotalExemption = 500_000L
    const val minimumTax = 5_000.0
    const val maxInvestmentRebate = 1_000_000.0
    const val incomeBasedInvestmentRebateRate = 0.03
    const val investmentRebateRate = 0.15

    val taxpayerTypes = listOf(
        TaxpayerType("general", "সাধারণ করদাতা", 375_000L),
        TaxpayerType("women", "মহিলা করদাতা", 400_000L),
        TaxpayerType("senior", "সিনিয়র সিটিজেন (৬৫+)", 400_000L),
        TaxpayerType("disabled", "প্রতিবন্ধী ব্যক্তি", 475_000L),
        TaxpayerType("freedomFighter", "মুক্তিযোদ্ধা", 500_000L)
    )

    val investmentOptions = listOf(
        InvestmentInputData("dse", "DSE শেয়ার"),
        InvestmentInputData("sanchaypatra", "সঞ্চয়পত্র"),
        InvestmentInputData("dps", "DPS (ডিপোজিট পেনশন স্কিম)"),
        InvestmentInputData("mutual", "মিউচুয়াল ফান্ড"),
        InvestmentInputData("insurance", "লাইফ ইন্স্যুরেন্স")
    )

    val taxSlabs = listOf(
        "প্রথম ৩,৭৫,০০০ টাকা পর্যন্ত" to "শূন্য",
        "পরবর্তী ৩,০০,০০০ টাকা" to "১০%",
        "পরবর্তী ৪,০০,০০০ টাকা" to "১৫%",
        "পরবর্তী ৫,০০,০০০ টাকা" to "২০%",
        "পরবর্তী ২০,০০,০০০ টাকা" to "২৫%",
        "অবশিষ্ট আয়" to "৩০%"
    )
}
