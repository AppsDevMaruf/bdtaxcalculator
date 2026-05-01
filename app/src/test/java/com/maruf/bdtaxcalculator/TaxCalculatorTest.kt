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
    fun `taxpayer limits match fiscal year 2025-26 rules`() {
        val limits = TaxDefaults.taxpayerTypes.associate { it.id to it.taxFreeLimit }

        assertEquals(375_000L, limits["general"])
        assertEquals(425_000L, limits["women"])
        assertEquals(425_000L, limits["senior"])
        assertEquals(500_000L, limits["disabled"])
        assertEquals(525_000L, limits["freedomFighter"])
    }

    @Test
    fun `salary breakdown applies exemption caps`() {
        val breakdown = calculateSalaryBreakdown(grossSalary = 100_000L, yearlyBonus = 200_000L)

        assertEquals(1_400_000L, breakdown.totalIncome)
        assertEquals(466_666L, breakdown.totalExemption)
        assertEquals(933_334L, breakdown.taxableIncome)
    }

    @Test
    fun `salary breakdown uses excel salary component formula`() {
        val breakdown = calculateSalaryBreakdown(grossSalary = 65_000L, yearlyBonus = 17_000L)

        assertEquals(38_594L, breakdown.basicSalary)
        assertEquals(19_297L, breakdown.houseRent)
        assertEquals(3_859L, breakdown.medical)
        assertEquals(3_250L, breakdown.conveyance)
        assertEquals(0L, breakdown.otherAllowances)
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
    fun `investment rebate is capped at ten lac`() {
        val rebate = calculateInvestmentRebate(
            investments = listOf(InvestmentInputData("dps", "DPS", "10000000")),
            taxableIncome = 50_000_000L
        )

        assertEquals(1_000_000.0, rebate, 0.001)
    }

    @Test
    fun `minimum tax is preserved even with investment rebate`() {
        val result = calculateTax(
            income = 400_000L,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 25_000.0
        )

        assertTrue(result.isMinimumTax)
        assertEquals(2_500.0, result.totalTax, 0.001)
        assertEquals(5_000.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `new assessment can use one thousand minimum tax`() {
        val result = calculateTax(
            income = 380_000L,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 0.0,
            minimumTax = TaxDefaults.newAssessmentMinimumTax
        )

        assertTrue(result.isMinimumTax)
        assertEquals(500.0, result.totalTax, 0.001)
        assertEquals(1_000.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `total tax is before rebate and final tax is after rebate`() {
        val result = calculateTax(
            income = 1_075_000L,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 20_000.0
        )

        assertEquals(90_000.0, result.totalTax, 0.001)
        assertEquals(70_000.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `matches provided excel sample for 50000 gross and 60000 bonus`() {
        val breakdown = calculateSalaryBreakdown(grossSalary = 50_000L, yearlyBonus = 60_000L)
        val result = calculateTax(
            income = breakdown.taxableIncome,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 0.0
        )

        assertEquals(660_000L, breakdown.totalIncome)
        assertEquals(220_000L, breakdown.totalExemption)
        assertEquals(440_000L, breakdown.taxableIncome)
        assertEquals(6_500.0, result.totalTax, 0.001)
        assertEquals(6_500.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `matches fiscal year 2025-26 rules for 65000 gross and 78000 bonus`() {
        val breakdown = calculateSalaryBreakdown(grossSalary = 65_000L, yearlyBonus = 78_000L)
        val result = calculateTax(
            income = breakdown.taxableIncome,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 0.0
        )

        assertEquals(858_000L, breakdown.totalIncome)
        assertEquals(286_000L, breakdown.totalExemption)
        assertEquals(572_000L, breakdown.taxableIncome)
        assertEquals(19_700.0, result.totalTax, 0.001)
        assertEquals(19_700.0, result.taxAfterRebate, 0.001)
    }
}
