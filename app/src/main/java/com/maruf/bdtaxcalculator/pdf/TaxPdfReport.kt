package com.maruf.bdtaxcalculator.pdf

import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.SalaryBreakdown
import com.maruf.bdtaxcalculator.tax.TaxPaymentAdjustment
import com.maruf.bdtaxcalculator.tax.TaxResult
import com.maruf.bdtaxcalculator.tax.TaxYearRules

data class TaxPdfInvestment(
    val type: String,
    val title: String,
    val amount: Long
)

data class TaxPdfReport(
    val isBangla: Boolean,
    val taxpayerType: String,
    val assessmentType: String,
    val taxpayerLocation: String,
    val disabledDependentCount: Int,
    val effectiveTaxFreeLimit: Long,
    val rules: TaxYearRules,
    val salary: SalaryBreakdown,
    val investments: List<TaxPdfInvestment>,
    val result: TaxResult,
    val payment: TaxPaymentAdjustment
) {
    val totalInvestment: Long = investments.sumOf(TaxPdfInvestment::amount)
}

fun buildTaxPdfInvestments(
    investments: List<InvestmentInputData>,
    titleFor: (InvestmentInputData) -> String
): List<TaxPdfInvestment> = investments.mapNotNull { investment ->
    val amount = investment.amount.toLongOrNull()?.takeIf { it > 0L } ?: return@mapNotNull null
    TaxPdfInvestment(type = investment.type, title = titleFor(investment), amount = amount)
}
