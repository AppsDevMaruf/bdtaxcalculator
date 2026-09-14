package com.maruf.bdtaxcalculator

import androidx.lifecycle.SavedStateHandle
import com.maruf.bdtaxcalculator.tax.LocalTaxPreferenceStore
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.TaxpayerLocation
import com.maruf.bdtaxcalculator.tax.TaxYearCatalog
import com.maruf.bdtaxcalculator.ui.screen.TaxCalculatorUiState
import com.maruf.bdtaxcalculator.ui.screen.TaxCalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaxCalculatorViewModelTest {
    private val defaults = TaxCalculatorUiState(
        selectedTaxpayerType = TaxDefaults.taxpayerTypes.first().id,
        selectedAssessmentType = LocalTaxPreferenceStore.assessmentRegular,
        selectedIncomeYear = TaxYearCatalog.current.incomeYear,
        selectedTaxpayerLocationId = TaxpayerLocation.DhakaOrChattogramCity.id
    )

    @Test
    fun updatesAreRestoredFromSavedState() {
        val handle = SavedStateHandle()
        val viewModel = TaxCalculatorViewModel(handle, defaults)

        viewModel.setGrossSalary("1200000")
        viewModel.setOtherIncome("125000")
        viewModel.setDisabledDependentCount(2)
        val investment = TaxDefaults.investmentOptions.first()
        viewModel.addInvestment(investment.type)
        viewModel.updateInvestment(investment.type, "50000")

        val restored = TaxCalculatorViewModel(handle, defaults).uiState.value
        assertEquals("1200000", restored.grossSalary)
        assertEquals("125000", restored.otherIncome)
        assertEquals(2, restored.disabledDependentCount)
        assertEquals("50000", restored.investments.single().amount)
    }

    @Test
    fun duplicateInvestmentIsIgnoredAndDependentCountIsBounded() {
        val viewModel = TaxCalculatorViewModel(SavedStateHandle(), defaults)
        val investmentType = TaxDefaults.investmentOptions.first().type

        viewModel.addInvestment(investmentType)
        viewModel.addInvestment(investmentType)
        viewModel.setDisabledDependentCount(Int.MAX_VALUE)

        assertEquals(1, viewModel.uiState.value.investments.size)
        assertTrue(viewModel.uiState.value.disabledDependentCount < Int.MAX_VALUE)
    }

    @Test
    fun resetClearsUserInputAndRestoresDefaults() {
        val viewModel = TaxCalculatorViewModel(SavedStateHandle(), defaults)
        viewModel.setGrossSalary("900000")
        viewModel.setOtherIncome("50000")
        viewModel.addInvestment(TaxDefaults.investmentOptions.first().type)

        viewModel.reset()

        assertEquals(defaults, viewModel.uiState.value)
    }
}
