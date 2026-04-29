package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.calculateInvestmentRebate
import com.maruf.bdtaxcalculator.tax.calculateSalaryBreakdown
import com.maruf.bdtaxcalculator.tax.calculateTax
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaxCalculatorTest {

    @Test
    fun `salary breakdown applies exemption caps`() {
        val breakdown = calculateSalaryBreakdown(grossSalary = 100_000L, yearlyBonus = 200_000L)

        assertEquals(1_400_000L, breakdown.totalIncome)
        assertEquals(390_000L, breakdown.totalExemption)
        assertEquals(1_010_000L, breakdown.taxableIncome)
    }

    @Test
    fun `investment rebate is capped by taxable income rule`() {
        val rebate = calculateInvestmentRebate(
            investments = listOf(InvestmentInputData("dps", "DPS", "900000")),
            taxableIncome = 600_000L
        )

        assertEquals(18_000.0, rebate, 0.001)
    }

    @Test
    fun `minimum tax is preserved even with investment rebate`() {
        val result = calculateTax(
            income = 400_000L,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 25_000.0
        )

        assertTrue(result.isMinimumTax)
        assertEquals(5_000.0, result.taxAfterRebate, 0.001)
    }
}
