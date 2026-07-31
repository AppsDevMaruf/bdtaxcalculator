package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.TaxpayerLocation
import com.maruf.bdtaxcalculator.tax.TaxYearCatalog
import com.maruf.bdtaxcalculator.tax.calculateInvestmentRebate
import com.maruf.bdtaxcalculator.tax.calculateSalaryBreakdown
import com.maruf.bdtaxcalculator.tax.calculateTax
import com.maruf.bdtaxcalculator.tax.calculateTaxFreeLimit
import com.maruf.bdtaxcalculator.tax.calculateTaxPaymentAdjustment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaxCalculatorTest {

    @Test
    fun `GPF is available as an investment rebate input`() {
        val gpf = TaxDefaults.investmentOptions.first()

        assertEquals("gpf", gpf.type)
        assertEquals("GPF-এ নিজস্ব জমা", gpf.title)
    }

    @Test
    fun `DPS rebate only counts the statutory eligible limit`() {
        val dps = TaxDefaults.investmentOptions.first { it.type == "dps" }.copy(amount = "200000")

        val rebate = calculateInvestmentRebate(
            investments = listOf(dps),
            taxableIncome = 2_000_000L
        )

        assertEquals(12_000.0, rebate, 0.001)
    }

    @Test
    fun `government securities and mutual funds apply their own limits`() {
        val governmentSecurities = TaxDefaults.investmentOptions
            .first { it.type == "sanchaypatra" }
            .copy(amount = "600000")
        val mutualFund = TaxDefaults.investmentOptions
            .first { it.type == "mutual" }
            .copy(amount = "700000")

        val rebate = calculateInvestmentRebate(
            investments = listOf(governmentSecurities, mutualFund),
            taxableIncome = 10_000_000L
        )

        assertEquals(100_000.0, rebate, 0.001)
    }

    @Test
    fun `new Act category caps are not retroactively applied to older income years`() {
        val dps = TaxDefaults.investmentOptions.first { it.type == "dps" }.copy(amount = "200000")

        val rebate = calculateInvestmentRebate(
            investments = listOf(dps),
            taxableIncome = 2_000_000L,
            rules = TaxYearCatalog.find("2022-23")
        )

        assertEquals(30_000.0, rebate, 0.001)
    }

    @Test
    fun `taxpayer limits match assessment year 2026-27 rules`() {
        val limits = TaxDefaults.taxpayerTypes.associate { it.id to it.taxFreeLimit }

        assertEquals(400_000L, limits["general"])
        assertEquals(450_000L, limits["women"])
        assertEquals(450_000L, limits["senior"])
        assertEquals(525_000L, limits["thirdGender"])
        assertEquals(525_000L, limits["disabled"])
        assertEquals(550_000L, limits["freedomFighter"])
    }

    @Test
    fun `disabled dependent allowance increases tax free limit by fifty thousand each`() {
        val limit = calculateTaxFreeLimit(
            baseTaxFreeLimit = 400_000L,
            disabledDependentCount = 2
        )

        assertEquals(500_000L, limit)
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
    fun `investment rebate uses ten percent of actual eligible investment`() {
        val rebate = calculateInvestmentRebate(
            investments = listOf(InvestmentInputData("dps", "DPS", "900000")),
            taxableIncome = 10_000_000L
        )

        assertEquals(90_000.0, rebate, 0.001)
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
    fun `investment rebate is capped at seven point five lac`() {
        val rebate = calculateInvestmentRebate(
            investments = listOf(InvestmentInputData("dps", "DPS", "10000000")),
            taxableIncome = 50_000_000L
        )

        assertEquals(750_000.0, rebate, 0.001)
    }

    @Test
    fun `minimum tax is preserved even with investment rebate`() {
        val result = calculateTax(
            income = 425_000L,
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
            income = 405_000L,
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
            income = 1_100_000L,
            taxFreeLimit = TaxDefaults.taxpayerTypes.first().taxFreeLimit,
            investmentRebate = 20_000.0
        )

        assertEquals(90_000.0, result.totalTax, 0.001)
        assertEquals(70_000.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `source and advance tax credits reduce remaining payable tax`() {
        val adjustment = calculateTaxPaymentAdjustment(
            taxLiability = 70_000.0,
            adjustableSourceTax = 20_000L,
            advanceTax = 10_000L
        )

        assertEquals(30_000.0, adjustment.totalTaxCredit, 0.001)
        assertEquals(40_000.0, adjustment.remainingPayable, 0.001)
        assertEquals(0.0, adjustment.excessPaid, 0.001)
    }

    @Test
    fun `tax credits above liability are reported as excess paid`() {
        val adjustment = calculateTaxPaymentAdjustment(
            taxLiability = 50_000.0,
            adjustableSourceTax = 40_000L,
            advanceTax = 20_000L
        )

        assertEquals(0.0, adjustment.remainingPayable, 0.001)
        assertEquals(10_000.0, adjustment.excessPaid, 0.001)
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
        assertEquals(4_000.0, result.totalTax, 0.001)
        assertEquals(5_000.0, result.taxAfterRebate, 0.001)
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
        assertEquals(17_200.0, result.totalTax, 0.001)
        assertEquals(17_200.0, result.taxAfterRebate, 0.001)
    }

    @Test
    fun `catalog contains five consecutive income years`() {
        assertEquals(
            listOf("2025-26", "2024-25", "2023-24", "2022-23", "2021-22"),
            TaxYearCatalog.supportedYears.map { it.incomeYear }
        )
        assertEquals(
            listOf("2026-27", "2025-26", "2024-25", "2023-24", "2022-23"),
            TaxYearCatalog.supportedYears.map { it.assessmentYear }
        )
        assertEquals(
            5,
            TaxYearCatalog.supportedYears.count {
                it.officialSourceUrl.startsWith("https://nbr.gov.bd/") &&
                    it.officialSourceUrl.endsWith(".pdf")
            }
        )
    }

    @Test
    fun `historical general thresholds match official schedules`() {
        val thresholds = TaxYearCatalog.supportedYears.associate { rules ->
            rules.incomeYear to rules.taxpayerTypes.first { it.id == "general" }.taxFreeLimit
        }

        assertEquals(400_000L, thresholds["2025-26"])
        assertEquals(350_000L, thresholds["2024-25"])
        assertEquals(350_000L, thresholds["2023-24"])
        assertEquals(350_000L, thresholds["2022-23"])
        assertEquals(300_000L, thresholds["2021-22"])
    }

    @Test
    fun `historical minimum tax follows taxpayer location`() {
        val rules = TaxYearCatalog.find("2023-24")

        assertEquals(5_000.0, rules.minimumTax("regular", TaxpayerLocation.DhakaOrChattogramCity), 0.001)
        assertEquals(4_000.0, rules.minimumTax("regular", TaxpayerLocation.OtherCityCorporation), 0.001)
        assertEquals(3_000.0, rules.minimumTax("regular", TaxpayerLocation.OutsideCityCorporation), 0.001)
    }

    @Test
    fun `oldest supported year keeps distinct third gender and disability limits`() {
        val limits = TaxYearCatalog.find("2021-22").taxpayerTypes.associate { it.id to it.taxFreeLimit }

        assertEquals(350_000L, limits["thirdGender"])
        assertEquals(450_000L, limits["disabled"])
    }

    @Test
    fun `tax calculation uses each selected years slabs`() {
        val income = 1_000_000L
        val currentRules = TaxYearCatalog.find("2025-26")
        val previousRules = TaxYearCatalog.find("2024-25")
        val olderRules = TaxYearCatalog.find("2022-23")

        assertEquals(75_000.0, calculateTax(income, 400_000L, slabs = currentRules.slabs).totalTax, 0.001)
        assertEquals(67_500.0, calculateTax(income, 350_000L, slabs = previousRules.slabs).totalTax, 0.001)
        assertEquals(72_500.0, calculateTax(income, 350_000L, slabs = olderRules.slabs).totalTax, 0.001)
    }
}
