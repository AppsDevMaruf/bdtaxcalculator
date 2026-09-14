package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.tax.calculateSalaryBreakdown
import org.junit.Assert.assertEquals
import org.junit.Test

class OtherIncomeTest {
    @Test fun otherIncomeDoesNotIncreaseSalaryExemption() {
        val salary = calculateSalaryBreakdown(50000, 25000)
        val combined = calculateSalaryBreakdown(50000, 25000, otherIncome = 120000)
        assertEquals(salary.totalExemption, combined.totalExemption)
        assertEquals(salary.totalIncome + 120000, combined.totalIncome)
        assertEquals(salary.taxableIncome + 120000, combined.taxableIncome)
        assertEquals(120000L, combined.otherIncome)
    }

    @Test fun otherIncomeAloneIsNotSalaryExempt() {
        val result = calculateSalaryBreakdown(0, 0, otherIncome = 600000)
        assertEquals(0L, result.totalExemption)
        assertEquals(600000L, result.taxableIncome)
        assertEquals(600000L, result.totalIncome)
    }

    @Test fun zeroAndNegativeOtherIncomePreserveSalaryCalculation() {
        val original = calculateSalaryBreakdown(40000, 10000)
        assertEquals(original, calculateSalaryBreakdown(40000, 10000, otherIncome = 0))
        assertEquals(original, calculateSalaryBreakdown(40000, 10000, otherIncome = -10))
    }
}
