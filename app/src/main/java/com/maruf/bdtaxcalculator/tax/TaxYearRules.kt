package com.maruf.bdtaxcalculator.tax

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Accessible
import androidx.compose.material.icons.filled.Elderly
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material.icons.filled.Woman

data class TaxSlabRule(
    val amount: Long,
    val rate: Double
)

enum class SalaryExemptionMethod {
    StandardOneThird,
    LegacyAllowanceBased
}

enum class TaxpayerLocation(val id: String, val minimumTax: Double) {
    DhakaOrChattogramCity("dhaka_chattogram_city", 5_000.0),
    OtherCityCorporation("other_city_corporation", 4_000.0),
    OutsideCityCorporation("outside_city_corporation", 3_000.0)
}

data class TaxYearRules(
    val incomeYear: String,
    val assessmentYear: String,
    val officialSourceUrl: String,
    val taxpayerTypes: List<TaxpayerType>,
    val slabs: List<TaxSlabRule>,
    val salaryExemptionMethod: SalaryExemptionMethod,
    val salaryExemptionCap: Long,
    val investmentRebateRate: Double,
    val incomeBasedInvestmentRebateRate: Double,
    val maxInvestmentRebate: Double,
    val supportsNewTaxpayerMinimum: Boolean,
    val disabledDependentAllowance: Long = 50_000L
) {
    val id: String = incomeYear

    fun minimumTax(
        assessmentType: String,
        location: TaxpayerLocation
    ): Double {
        return if (
            supportsNewTaxpayerMinimum &&
            assessmentType == LocalTaxPreferenceStore.assessmentNew
        ) {
            1_000.0
        } else if (supportsNewTaxpayerMinimum) {
            5_000.0
        } else {
            location.minimumTax
        }
    }
}

object TaxYearCatalog {
    // Official NBR sources:
    // https://nbr.gov.bd/uploads/paripatra/Income-tax_Paripatra_2025-20261.pdf
    // https://nbr.gov.bd/taxtypes/income-tax/income-tax-paripatra/eng
    val supportedYears: List<TaxYearRules> = listOf(
        rules(
            incomeYear = "2025-26",
            assessmentYear = "2026-27",
            officialSourceUrl = "https://nbr.gov.bd/uploads/paripatra/Income-tax_Paripatra_2025-20261.pdf",
            limits = Limits(400_000L, 450_000L, 450_000L, 525_000L, 525_000L, 550_000L),
            slabs = listOf(
                TaxSlabRule(300_000L, 0.10),
                TaxSlabRule(400_000L, 0.15),
                TaxSlabRule(500_000L, 0.20),
                TaxSlabRule(2_000_000L, 0.25),
                TaxSlabRule(Long.MAX_VALUE, 0.30)
            ),
            salaryExemptionCap = 500_000L,
            investmentRebateRate = 0.10,
            maxInvestmentRebate = 750_000.0,
            supportsNewTaxpayerMinimum = true
        ),
        rules(
            incomeYear = "2024-25",
            assessmentYear = "2025-26",
            officialSourceUrl = "https://nbr.gov.bd/uploads/paripatra/%E0%A6%86%E0%A7%9F%E0%A6%95%E0%A6%B0_%E0%A6%AA%E0%A6%B0%E0%A6%BF%E0%A6%AA%E0%A6%A4%E0%A7%8D%E0%A6%B0_%E0%A7%A8%E0%A7%A6%E0%A7%A8%E0%A7%AA-%E0%A7%A8%E0%A7%AB_%28%E0%A6%9A%E0%A7%82%E0%A7%9C%E0%A6%BE%E0%A6%A8%E0%A7%8D%E0%A6%A4%29.pdf",
            limits = Limits(350_000L, 400_000L, 400_000L, 475_000L, 475_000L, 500_000L),
            slabs = listOf(
                TaxSlabRule(100_000L, 0.05),
                TaxSlabRule(400_000L, 0.10),
                TaxSlabRule(500_000L, 0.15),
                TaxSlabRule(500_000L, 0.20),
                TaxSlabRule(2_000_000L, 0.25),
                TaxSlabRule(Long.MAX_VALUE, 0.30)
            ),
            salaryExemptionCap = 500_000L
        ),
        rules(
            incomeYear = "2023-24",
            assessmentYear = "2024-25",
            officialSourceUrl = "https://nbr.gov.bd/uploads/paripatra/%E0%A6%86%E0%A7%9F%E0%A6%95%E0%A6%B0_%E0%A6%AA%E0%A6%B0%E0%A6%BF%E0%A6%AA%E0%A6%A4%E0%A7%8D%E0%A6%B0_%E0%A7%A8%E0%A7%A6%E0%A7%A8%E0%A7%A9-%E0%A7%A8%E0%A7%AA.pdf",
            limits = Limits(350_000L, 400_000L, 400_000L, 475_000L, 475_000L, 500_000L),
            slabs = listOf(
                TaxSlabRule(100_000L, 0.05),
                TaxSlabRule(400_000L, 0.10),
                TaxSlabRule(500_000L, 0.15),
                TaxSlabRule(500_000L, 0.20),
                TaxSlabRule(Long.MAX_VALUE, 0.25)
            ),
            salaryExemptionCap = 450_000L
        ),
        rules(
            incomeYear = "2022-23",
            assessmentYear = "2023-24",
            officialSourceUrl = "https://nbr.gov.bd/uploads/paripatra/Paripatra_2022-2023_.pdf",
            limits = Limits(350_000L, 400_000L, 400_000L, 475_000L, 475_000L, 500_000L),
            slabs = listOf(
                TaxSlabRule(100_000L, 0.05),
                TaxSlabRule(300_000L, 0.10),
                TaxSlabRule(400_000L, 0.15),
                TaxSlabRule(500_000L, 0.20),
                TaxSlabRule(Long.MAX_VALUE, 0.25)
            ),
            salaryExemptionCap = 450_000L
        ),
        rules(
            incomeYear = "2021-22",
            assessmentYear = "2022-23",
            officialSourceUrl = "https://nbr.gov.bd/uploads/paripatra/2021_Paripatra_draft_final.pdf",
            limits = Limits(300_000L, 350_000L, 350_000L, 350_000L, 450_000L, 475_000L),
            slabs = listOf(
                TaxSlabRule(100_000L, 0.05),
                TaxSlabRule(300_000L, 0.10),
                TaxSlabRule(400_000L, 0.15),
                TaxSlabRule(500_000L, 0.20),
                TaxSlabRule(Long.MAX_VALUE, 0.25)
            ),
            salaryExemptionMethod = SalaryExemptionMethod.LegacyAllowanceBased,
            salaryExemptionCap = 0L,
            maxInvestmentRebate = 1_500_000.0
        )
    )

    val current: TaxYearRules = supportedYears.first()

    fun find(incomeYear: String): TaxYearRules =
        supportedYears.firstOrNull { it.id == incomeYear } ?: current

    private fun rules(
        incomeYear: String,
        assessmentYear: String,
        officialSourceUrl: String,
        limits: Limits,
        slabs: List<TaxSlabRule>,
        salaryExemptionMethod: SalaryExemptionMethod = SalaryExemptionMethod.StandardOneThird,
        salaryExemptionCap: Long,
        investmentRebateRate: Double = 0.15,
        incomeBasedInvestmentRebateRate: Double = 0.03,
        maxInvestmentRebate: Double = 1_000_000.0,
        supportsNewTaxpayerMinimum: Boolean = false
    ) = TaxYearRules(
        incomeYear = incomeYear,
        assessmentYear = assessmentYear,
        officialSourceUrl = officialSourceUrl,
        taxpayerTypes = listOf(
            TaxpayerType("general", "সাধারণ করদাতা", limits.general, Icons.Default.Person),
            TaxpayerType("women", "মহিলা করদাতা", limits.women, Icons.Default.Woman),
            TaxpayerType("senior", "সিনিয়র সিটিজেন (৬৫+)", limits.senior, Icons.Default.Elderly),
            TaxpayerType("thirdGender", "তৃতীয় লিঙ্গ", limits.thirdGender, Icons.Default.Transgender),
            TaxpayerType("disabled", "প্রতিবন্ধী", limits.disabled, Icons.AutoMirrored.Filled.Accessible),
            TaxpayerType("freedomFighter", "যুদ্ধাহত মুক্তিযোদ্ধা", limits.freedomFighter, Icons.Default.MilitaryTech)
        ),
        slabs = slabs,
        salaryExemptionMethod = salaryExemptionMethod,
        salaryExemptionCap = salaryExemptionCap,
        investmentRebateRate = investmentRebateRate,
        incomeBasedInvestmentRebateRate = incomeBasedInvestmentRebateRate,
        maxInvestmentRebate = maxInvestmentRebate,
        supportsNewTaxpayerMinimum = supportsNewTaxpayerMinimum
    )

    private data class Limits(
        val general: Long,
        val women: Long,
        val senior: Long,
        val thirdGender: Long,
        val disabled: Long,
        val freedomFighter: Long
    )
}
