package com.maruf.bdtaxcalculator.tax

import kotlin.math.roundToLong

fun calculateSalaryBreakdown(grossSalary: Long, yearlyBonus: Long): SalaryBreakdown {
    val basicSalary = (grossSalary * 0.50).toLong()
    val houseRent = (basicSalary * 0.50).toLong()
    val medical = (basicSalary * 0.10).toLong()
    val conveyance = (grossSalary * 0.05).toLong()
    val otherAllowances = grossSalary - (basicSalary + houseRent + medical + conveyance)

    val totalIncome = (grossSalary * 12) + yearlyBonus
    val yearlyHouseRent = houseRent * 12
    val yearlyMedical = medical * 12
    val yearlyConveyance = conveyance * 12

    val totalIndividualExemption = minOf(
        yearlyHouseRent,
        TaxDefaults.maxHouseRentExemption
    ) + minOf(
        yearlyMedical,
        TaxDefaults.maxMedicalExemption
    ) + minOf(
        yearlyConveyance,
        TaxDefaults.maxConveyanceExemption
    )

    val totalExemption = minOf(
        totalIndividualExemption,
        minOf(totalIncome / 3, TaxDefaults.maxTotalExemption)
    )

    return SalaryBreakdown(
        grossSalary = grossSalary,
        basicSalary = basicSalary,
        houseRent = houseRent,
        medical = medical,
        conveyance = conveyance,
        otherAllowances = otherAllowances,
        yearlyBonus = yearlyBonus,
        totalIncome = totalIncome,
        totalExemption = totalExemption,
        taxableIncome = maxOf(0L, totalIncome - totalExemption)
    )
}

fun calculateInvestmentRebate(investments: List<InvestmentInputData>, taxableIncome: Long): Double {
    val totalInvestment = investments.sumOf { it.amount.toLongOrNull() ?: 0L }
    val rebateByInvestment = totalInvestment * TaxDefaults.investmentRebateRate
    val rebateByIncome = taxableIncome * TaxDefaults.incomeBasedInvestmentRebateRate
    return minOf(rebateByInvestment, rebateByIncome, TaxDefaults.maxInvestmentRebate)
}

fun calculateTax(
    income: Long,
    taxFreeLimit: Long,
    investmentRebate: Double = 0.0
): TaxResult {
    if (income <= taxFreeLimit) {
        return TaxResult(
            totalTax = 0.0,
            breakdown = emptyList(),
            taxableAmount = 0L,
            isMinimumTax = false,
            investmentRebate = investmentRebate,
            taxAfterRebate = 0.0
        )
    }

    val slabs = listOf(
        300_000L to 0.10,
        400_000L to 0.15,
        500_000L to 0.20,
        2_000_000L to 0.25,
        Long.MAX_VALUE to 0.30
    )

    var remainingIncome = income - taxFreeLimit
    var currentStart = taxFreeLimit
    var rawTax = 0.0
    val breakdown = mutableListOf<TaxBreakdown>()

    for ((slabSize, rate) in slabs) {
        if (remainingIncome <= 0) break

        val taxableInSlab = minOf(remainingIncome, slabSize)
        val slabTax = taxableInSlab * rate

        breakdown += TaxBreakdown(
            label = "৳${formatBengaliNumber(currentStart)} থেকে পরবর্তী ৳${formatBengaliNumber(taxableInSlab)}",
            amount = taxableInSlab,
            rate = rate * 100,
            tax = slabTax
        )

        rawTax += slabTax
        remainingIncome -= taxableInSlab
        currentStart += taxableInSlab
    }

    val taxBeforeRebate = if (rawTax in 0.0..<TaxDefaults.minimumTax) {
        TaxDefaults.minimumTax
    } else {
        rawTax
    }

    val taxAfterRebate = if (taxBeforeRebate == TaxDefaults.minimumTax) {
        TaxDefaults.minimumTax
    } else {
        maxOf(taxBeforeRebate - investmentRebate, 0.0)
    }

    return TaxResult(
        totalTax = taxAfterRebate,
        breakdown = breakdown,
        taxableAmount = income - taxFreeLimit,
        isMinimumTax = taxBeforeRebate == TaxDefaults.minimumTax,
        investmentRebate = investmentRebate,
        taxAfterRebate = taxAfterRebate
    )
}

fun calculateTaxSummary(totalIncome: Long, taxAfterRebate: Double): TaxSummary {
    val yearlyTax = taxAfterRebate.roundToLong()
    val monthlyTaxEstimate = (yearlyTax / 12.0).roundToLong()
    val effectiveTaxRatePercent = if (totalIncome == 0L) 0.0 else (yearlyTax * 100.0) / totalIncome

    return TaxSummary(
        monthlyTaxEstimate = monthlyTaxEstimate,
        yearlyNetIncomeAfterTax = maxOf(0L, totalIncome - yearlyTax),
        effectiveTaxRatePercent = effectiveTaxRatePercent
    )
}
