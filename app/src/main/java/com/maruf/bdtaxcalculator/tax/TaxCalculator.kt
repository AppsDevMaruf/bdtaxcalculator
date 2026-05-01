package com.maruf.bdtaxcalculator.tax

import kotlin.math.roundToLong

fun calculateSalaryBreakdown(grossSalary: Long, yearlyBonus: Long): SalaryBreakdown {
    val conveyance = (grossSalary * 0.05).roundToLong()
    val basicSalary = ((grossSalary - conveyance) / 1.6).roundToLong()
    val houseRent = (basicSalary * 0.50).roundToLong()
    val medical = (basicSalary * 0.10).roundToLong()
    val otherAllowances = grossSalary - (basicSalary + houseRent + medical + conveyance)

    val totalIncome = (grossSalary * 12) + yearlyBonus
    val totalExemption = minOf(
        totalIncome / 3,
        TaxDefaults.maxTotalExemption
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
    investmentRebate: Double = 0.0,
    minimumTax: Double = TaxDefaults.minimumTax
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

    val taxAfterRebate = when {
        rawTax <= 0.0 -> 0.0
        rawTax - investmentRebate > minimumTax -> rawTax - investmentRebate
        else -> minimumTax
    }

    return TaxResult(
        totalTax = rawTax,
        breakdown = breakdown,
        taxableAmount = income - taxFreeLimit,
        isMinimumTax = taxAfterRebate == minimumTax,
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
