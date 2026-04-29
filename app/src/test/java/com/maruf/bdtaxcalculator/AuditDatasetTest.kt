package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.audit.AuditDataset
import com.maruf.bdtaxcalculator.audit.AuditRecord
import com.maruf.bdtaxcalculator.audit.maskTin
import com.maruf.bdtaxcalculator.audit.normalizeTin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditDatasetTest {

    @Test
    fun `normalize tin keeps only digits`() {
        assertEquals("383899434271", normalizeTin("3838-9943 4271"))
    }

    @Test
    fun `mask tin hides middle digits`() {
        assertEquals("38••••••••71", maskTin("383899434271"))
    }

    @Test
    fun `lookup resolves zone circle and selection flag`() {
        val dataset = AuditDataset(
            zones = listOf("Zone A"),
            circles = listOf("Circle 1"),
            records = mapOf(
                "383899434271" to AuditRecord(0, 0, "Individual", "2023-2024", false),
                "111122223333" to AuditRecord(0, 0, "—", "2023-2024", true)
            )
        )

        val present = dataset.lookupTin("383899434271")
        val selected = dataset.lookupTin("111122223333")
        val missing = dataset.lookupTin("9999")

        assertNotNull(present)
        assertEquals("Zone A", present?.zone)
        assertFalse(present?.isSelected ?: true)
        assertTrue(selected?.isSelected ?: false)
        assertNull(missing)
    }
}
