package com.maruf.bdtaxcalculator.tax

data class TaxpayerType(
    val id: String,
    val label: String,
    val taxFreeLimit: Long
)

data class TaxBreakdown(
    val label: String,
    val amount: Long,
    val rate: Double,
    val tax: Double
)

data class TaxResult(
    val totalTax: Double,
    val breakdown: List<TaxBreakdown>,
    val taxableAmount: Long,
    val isMinimumTax: Boolean,
    val investmentRebate: Double = 0.0,
    val taxAfterRebate: Double = 0.0
)

data class InvestmentInputData(
    val type: String,
    val title: String,
    val amount: String = ""
)

data class SalaryBreakdown(
    val grossSalary: Long,
    val basicSalary: Long,
    val houseRent: Long,
    val medical: Long,
    val conveyance: Long,
    val otherAllowances: Long,
    val yearlyBonus: Long,
    val totalIncome: Long,
    val totalExemption: Long,
    val taxableIncome: Long
)

data class TaxSummary(
    val monthlyTaxEstimate: Long,
    val yearlyNetIncomeAfterTax: Long,
    val effectiveTaxRatePercent: Double
)
