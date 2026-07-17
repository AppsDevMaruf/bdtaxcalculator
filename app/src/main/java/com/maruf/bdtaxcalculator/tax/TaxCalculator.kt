package com.maruf.bdtaxcalculator.tax

import kotlin.math.roundToLong
import java.util.Locale

fun calculateSalaryBreakdown(
    grossSalary: Long,
    yearlyBonus: Long,
    rules: TaxYearRules = TaxYearCatalog.current
): SalaryBreakdown {
    val conveyance = (grossSalary * 0.05).roundToLong()
    val basicSalary = ((grossSalary - conveyance) / 1.6).roundToLong()
    val houseRent = (basicSalary * 0.50).roundToLong()
    val medical = (basicSalary * 0.10).roundToLong()
    val otherAllowances = grossSalary - (basicSalary + houseRent + medical + conveyance)

    val totalIncome = (grossSalary * 12) + yearlyBonus
    val totalExemption = when (rules.salaryExemptionMethod) {
        SalaryExemptionMethod.StandardOneThird -> minOf(
            totalIncome / 3,
            rules.salaryExemptionCap
        )
        SalaryExemptionMethod.LegacyAllowanceBased -> {
            val annualBasic = basicSalary * 12
            val annualHouseRent = houseRent * 12
            val annualMedical = medical * 12
            val annualConveyance = conveyance * 12
            val exemptHouseRent = minOf(annualHouseRent, annualBasic / 2, 300_000L)
            val exemptMedical = minOf(annualMedical, annualBasic / 10, 120_000L)
            val exemptConveyance = minOf(annualConveyance, 30_000L)
            exemptHouseRent + exemptMedical + exemptConveyance
        }
    }

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

fun calculateInvestmentRebate(
    investments: List<InvestmentInputData>,
    taxableIncome: Long,
    rules: TaxYearRules = TaxYearCatalog.current
): Double {
    val totalInvestment = investments.sumOf { it.amount.toLongOrNull() ?: 0L }
    val rebateByInvestment = totalInvestment * rules.investmentRebateRate
    val rebateByIncome = taxableIncome * rules.incomeBasedInvestmentRebateRate
    return minOf(rebateByInvestment, rebateByIncome, rules.maxInvestmentRebate)
}

fun calculateTaxFreeLimit(baseTaxFreeLimit: Long, disabledDependentCount: Int): Long {
    return calculateTaxFreeLimit(
        baseTaxFreeLimit = baseTaxFreeLimit,
        disabledDependentCount = disabledDependentCount,
        allowancePerDependent = TaxDefaults.disabledDependentAllowance
    )
}

fun calculateTaxFreeLimit(
    baseTaxFreeLimit: Long,
    disabledDependentCount: Int,
    allowancePerDependent: Long
): Long {
    return baseTaxFreeLimit +
        disabledDependentCount.coerceAtLeast(0).toLong() * allowancePerDependent
}

fun calculateTaxPaymentAdjustment(
    taxLiability: Double,
    adjustableSourceTax: Long,
    advanceTax: Long
): TaxPaymentAdjustment {
    val normalizedLiability = taxLiability.coerceAtLeast(0.0)
    val normalizedSourceTax = adjustableSourceTax.coerceAtLeast(0L).toDouble()
    val normalizedAdvanceTax = advanceTax.coerceAtLeast(0L).toDouble()
    val totalTaxCredit = normalizedSourceTax + normalizedAdvanceTax

    return TaxPaymentAdjustment(
        taxLiability = normalizedLiability,
        adjustableSourceTax = normalizedSourceTax,
        advanceTax = normalizedAdvanceTax,
        totalTaxCredit = totalTaxCredit,
        remainingPayable = (normalizedLiability - totalTaxCredit).coerceAtLeast(0.0),
        excessPaid = (totalTaxCredit - normalizedLiability).coerceAtLeast(0.0)
    )
}

fun calculateTax(
    income: Long,
    taxFreeLimit: Long,
    investmentRebate: Double = 0.0,
    minimumTax: Double = TaxDefaults.minimumTax,
    slabs: List<TaxSlabRule> = TaxYearCatalog.current.slabs
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

    var remainingIncome = income - taxFreeLimit
    var currentStart = taxFreeLimit
    var rawTax = 0.0
    val breakdown = mutableListOf<TaxBreakdown>()

    for (slab in slabs) {
        if (remainingIncome <= 0) break

        val taxableInSlab = minOf(remainingIncome, slab.amount)
        val slabTax = taxableInSlab * slab.rate

        breakdown += TaxBreakdown(
            label = formatTaxSlabLabel(currentStart, taxableInSlab),
            amount = taxableInSlab,
            rate = slab.rate * 100,
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

private fun formatTaxSlabLabel(currentStart: Long, taxableInSlab: Long): String {
    return if (Locale.getDefault().language == "bn") {
        "৳${formatBengaliNumber(currentStart)} থেকে পরবর্তী ৳${formatBengaliNumber(taxableInSlab)}"
    } else {
        "BDT ${formatBengaliNumber(currentStart)} to next BDT ${formatBengaliNumber(taxableInSlab)}"
    }
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
