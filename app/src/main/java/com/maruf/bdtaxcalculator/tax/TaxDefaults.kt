package com.maruf.bdtaxcalculator.tax

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Woman

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
        TaxpayerType("general", "সাধারণ করদাতা", 375_000L, Icons.Default.Person),
        TaxpayerType("women", "মহিলা করদাতা", 400_000L, Icons.Default.Woman),
        TaxpayerType("senior", "সিনিয়র সিটিজেন (৬৫+)", 400_000L, Icons.Default.Elderly),
        TaxpayerType("disabled", "প্রতিবন্ধী ব্যক্তি", 475_000L, Icons.AutoMirrored.Filled.Accessible),
        TaxpayerType("freedomFighter", "মুক্তিযোদ্ধা", 500_000L, Icons.Default.MilitaryTech)
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
