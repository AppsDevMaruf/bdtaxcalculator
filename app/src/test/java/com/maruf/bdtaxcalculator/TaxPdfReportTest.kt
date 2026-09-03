package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.pdf.buildTaxPdfInvestments
import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import org.junit.Assert.assertEquals
import org.junit.Test

class TaxPdfReportTest {
    @Test
    fun `pdf investments include only positive numeric entries`() {
        val investments = listOf(
            InvestmentInputData(type = "dps", title = "DPS", amount = "120000"),
            InvestmentInputData(type = "insurance", title = "Insurance", amount = "0"),
            InvestmentInputData(type = "gpf", title = "GPF", amount = "invalid")
        )

        val result = buildTaxPdfInvestments(investments) { it.title }

        assertEquals(1, result.size)
        assertEquals("dps", result.single().type)
        assertEquals("DPS", result.single().title)
        assertEquals(120_000L, result.single().amount)
    }
}
