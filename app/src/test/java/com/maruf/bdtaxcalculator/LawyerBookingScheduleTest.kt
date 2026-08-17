package com.maruf.bdtaxcalculator

import com.maruf.bdtaxcalculator.ui.screen.isConsultationScheduleInFuture
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LawyerBookingScheduleTest {
    private val formatter = SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.US)

    @Test
    fun pastScheduleIsRejected() {
        val now = System.currentTimeMillis()
        val past = formatter.format(Date(now - 60_000L)).splitSchedule()

        assertFalse(isConsultationScheduleInFuture(past.first, past.second, now))
    }

    @Test
    fun futureScheduleIsAccepted() {
        val now = System.currentTimeMillis()
        val future = formatter.format(Date(now + 3_600_000L)).splitSchedule()

        assertTrue(isConsultationScheduleInFuture(future.first, future.second, now))
    }

    @Test
    fun invalidOrIncompleteScheduleIsRejected() {
        assertFalse(isConsultationScheduleInFuture("", "", System.currentTimeMillis()))
        assertFalse(isConsultationScheduleInFuture("not-a-date", "9:00 AM", System.currentTimeMillis()))
    }

    private fun String.splitSchedule(): Pair<String, String> {
        return substring(0, 10) to substring(11)
    }
}
