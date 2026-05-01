package com.maruf.bdtaxcalculator.tax

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Woman

object TaxDefaults {
    const val taxYearLabel = "২০২৫-২৬"
    const val maxTotalExemption = 500_000L
    const val minimumTax = 5_000.0
    const val newAssessmentMinimumTax = 1_000.0
    const val maxInvestmentRebate = 1_000_000.0
    const val incomeBasedInvestmentRebateRate = 0.03
    const val investmentRebateRate = 0.15

    val taxpayerTypes = listOf(
        TaxpayerType("general", "সাধারণ করদাতা", 375_000L, Icons.Default.Person),
        TaxpayerType("women", "মহিলা করদাতা", 425_000L, Icons.Default.Woman),
        TaxpayerType("senior", "সিনিয়র সিটিজেন (৬৫+)", 425_000L, Icons.Default.Elderly),
        TaxpayerType("disabled", "তৃতীয় লিঙ্গ / প্রতিবন্ধী", 500_000L, Icons.AutoMirrored.Filled.Accessible),
        TaxpayerType("freedomFighter", "মুক্তিযোদ্ধা / জুলাই যোদ্ধা", 525_000L, Icons.Default.MilitaryTech)
    )

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
