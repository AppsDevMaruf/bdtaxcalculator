package com.maruf.bdtaxcalculator.ui.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.TaxDefaults

internal data class TaxCalculatorUiState(
    val grossSalary: String = "",
    val yearlyBonus: String = "",
    val otherIncome: String = "",
    val selectedTaxpayerType: String,
    val selectedAssessmentType: String,
    val selectedIncomeYear: String,
    val selectedTaxpayerLocationId: String,
    val disabledDependentCount: Int = 0,
    val adjustableSourceTax: String = "",
    val advanceTax: String = "",
    val investments: List<InvestmentInputData> = emptyList()
)

internal class TaxCalculatorViewModel(
    private val savedStateHandle: SavedStateHandle,
    defaults: TaxCalculatorUiState
) : ViewModel() {
    private val initialState = defaults

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(restore(defaults))
    val uiState: kotlinx.coroutines.flow.StateFlow<TaxCalculatorUiState> = _uiState

    fun setGrossSalary(value: String) = update { copy(grossSalary = value) }

    fun setYearlyBonus(value: String) = update { copy(yearlyBonus = value) }
    fun setOtherIncome(value: String) = update { copy(otherIncome = value) }

    fun setTaxpayerType(value: String) = update { copy(selectedTaxpayerType = value) }

    fun setAssessmentType(value: String) = update { copy(selectedAssessmentType = value) }

    fun setIncomeYear(value: String) = update { copy(selectedIncomeYear = value) }

    fun setTaxpayerLocation(value: String) = update { copy(selectedTaxpayerLocationId = value) }

    fun setDisabledDependentCount(value: Int) = update {
        copy(disabledDependentCount = value.coerceIn(0, MaxDisabledDependentCount))
    }

    fun setAdjustableSourceTax(value: String) = update { copy(adjustableSourceTax = value) }

    fun setAdvanceTax(value: String) = update { copy(advanceTax = value) }

    fun addInvestment(type: String) = update {
        val option = TaxDefaults.investmentOptions.firstOrNull { it.type == type }
        if (option == null || investments.any { it.type == type }) this
        else copy(investments = investments + option)
    }

    fun updateInvestment(type: String, amount: String) = update {
        copy(investments = investments.map { investment ->
            if (investment.type == type) investment.copy(amount = amount) else investment
        })
    }

    fun removeInvestment(type: String) = update {
        copy(investments = investments.filterNot { it.type == type })
    }

    fun reset() {
        _uiState.value = initialState
        persist(initialState)
    }

    private inline fun update(transform: TaxCalculatorUiState.() -> TaxCalculatorUiState) {
        val updated = _uiState.value.transform()
        if (updated != _uiState.value) {
            _uiState.value = updated
            persist(updated)
        }
    }

    private fun restore(defaults: TaxCalculatorUiState): TaxCalculatorUiState {
        val investmentTypes = savedStateHandle.get<ArrayList<String>>(KeyInvestmentTypes).orEmpty()
        val investmentAmounts = savedStateHandle.get<ArrayList<String>>(KeyInvestmentAmounts).orEmpty()
        val restoredInvestments = investmentTypes.mapIndexedNotNull { index, type ->
            TaxDefaults.investmentOptions.firstOrNull { it.type == type }
                ?.copy(amount = investmentAmounts.getOrElse(index) { "" })
        }
        return defaults.copy(
            grossSalary = savedStateHandle[KeyGrossSalary] ?: defaults.grossSalary,
            yearlyBonus = savedStateHandle[KeyYearlyBonus] ?: defaults.yearlyBonus,
            otherIncome = savedStateHandle[KeyOtherIncome] ?: defaults.otherIncome,
            selectedTaxpayerType = savedStateHandle[KeyTaxpayerType] ?: defaults.selectedTaxpayerType,
            selectedAssessmentType = savedStateHandle[KeyAssessmentType] ?: defaults.selectedAssessmentType,
            selectedIncomeYear = savedStateHandle[KeyIncomeYear] ?: defaults.selectedIncomeYear,
            selectedTaxpayerLocationId = savedStateHandle[KeyTaxpayerLocation] ?: defaults.selectedTaxpayerLocationId,
            disabledDependentCount = savedStateHandle[KeyDisabledDependents] ?: defaults.disabledDependentCount,
            adjustableSourceTax = savedStateHandle[KeyAdjustableSourceTax] ?: defaults.adjustableSourceTax,
            advanceTax = savedStateHandle[KeyAdvanceTax] ?: defaults.advanceTax,
            investments = restoredInvestments
        )
    }

    private fun persist(state: TaxCalculatorUiState) {
        savedStateHandle[KeyGrossSalary] = state.grossSalary
        savedStateHandle[KeyYearlyBonus] = state.yearlyBonus
        savedStateHandle[KeyOtherIncome] = state.otherIncome
        savedStateHandle[KeyTaxpayerType] = state.selectedTaxpayerType
        savedStateHandle[KeyAssessmentType] = state.selectedAssessmentType
        savedStateHandle[KeyIncomeYear] = state.selectedIncomeYear
        savedStateHandle[KeyTaxpayerLocation] = state.selectedTaxpayerLocationId
        savedStateHandle[KeyDisabledDependents] = state.disabledDependentCount
        savedStateHandle[KeyAdjustableSourceTax] = state.adjustableSourceTax
        savedStateHandle[KeyAdvanceTax] = state.advanceTax
        savedStateHandle[KeyInvestmentTypes] = ArrayList(state.investments.map { it.type })
        savedStateHandle[KeyInvestmentAmounts] = ArrayList(state.investments.map { it.amount })
    }

    companion object {
        private const val KeyGrossSalary = "gross_salary"
        private const val KeyYearlyBonus = "yearly_bonus"
        private const val KeyOtherIncome = "other_income"
        private const val KeyTaxpayerType = "taxpayer_type"
        private const val KeyAssessmentType = "assessment_type"
        private const val KeyIncomeYear = "income_year"
        private const val KeyTaxpayerLocation = "taxpayer_location"
        private const val KeyDisabledDependents = "disabled_dependents"
        private const val KeyAdjustableSourceTax = "adjustable_source_tax"
        private const val KeyAdvanceTax = "advance_tax"
        private const val KeyInvestmentTypes = "investment_types"
        private const val KeyInvestmentAmounts = "investment_amounts"

        fun factory(defaults: TaxCalculatorUiState): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T = TaxCalculatorViewModel(extras.createSavedStateHandle(), defaults) as T
            }
    }
}
