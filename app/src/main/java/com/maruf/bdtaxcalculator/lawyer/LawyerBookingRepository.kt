package com.maruf.bdtaxcalculator.lawyer

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

interface LawyerBookingRepository {
    fun observeLawyers(): Flow<List<LawyerProfile>>
    fun observeRequests(): Flow<List<LawyerBookingRequest>>
    fun saveDraft(request: LawyerBookingRequest)
    fun deleteDraft(id: String)
}

/**
 * Keeps the Android flow functional before the NestJS API is connected.
 * Only user-created drafts are stored; no unverified lawyer profiles are fabricated locally.
 */
class LocalLawyerBookingRepository(context: Context) : LawyerBookingRepository {
    private val preferences = context.applicationContext.getSharedPreferences(
        "lawyer_booking_drafts",
        Context.MODE_PRIVATE
    )
    private val lawyers = MutableStateFlow<List<LawyerProfile>>(emptyList())
    private val requests = MutableStateFlow(readRequests())

    override fun observeLawyers(): Flow<List<LawyerProfile>> = lawyers

    override fun observeRequests(): Flow<List<LawyerBookingRequest>> = requests

    override fun saveDraft(request: LawyerBookingRequest) {
        val updated = listOf(request.copy(status = BookingStatus.Draft)) +
            requests.value.filterNot { it.id == request.id }
        persist(updated)
    }

    override fun deleteDraft(id: String) {
        persist(requests.value.filterNot { it.id == id && it.status == BookingStatus.Draft })
    }

    private fun persist(updated: List<LawyerBookingRequest>) {
        requests.value = updated
        val json = JSONArray().apply {
            updated.forEach { request ->
                put(JSONObject().apply {
                    put("id", request.id)
                    put("lawyerId", request.lawyerId)
                    put("lawyerName", request.lawyerName)
                    put("clientName", request.clientName)
                    put("mobileNumber", request.mobileNumber)
                    put("clientEmail", request.clientEmail)
                    put("category", request.category.name)
                    put("mode", request.mode.name)
                    put("preferredDate", request.preferredDate)
                    put("preferredTime", request.preferredTime)
                    put("note", request.note)
                    put("status", request.status.name)
                    put("meetingUrl", request.meetingUrl)
                    put("contactConsentAtMillis", request.contactConsentAtMillis)
                    put("createdAtMillis", request.createdAtMillis)
                })
            }
        }
        preferences.edit().putString(KEY_REQUESTS, json.toString()).apply()
    }

    private fun readRequests(): List<LawyerBookingRequest> {
        val raw = preferences.getString(KEY_REQUESTS, null) ?: return emptyList()
        return runCatching {
            val json = JSONArray(raw)
            buildList {
                for (index in 0 until json.length()) {
                    val item = json.getJSONObject(index)
                    add(
                        LawyerBookingRequest(
                            id = item.getString("id"),
                            lawyerId = item.optString("lawyerId").takeIf { it.isNotBlank() },
                            lawyerName = item.optString("lawyerName").takeIf { it.isNotBlank() },
                            clientName = item.getString("clientName"),
                            mobileNumber = item.getString("mobileNumber"),
                            clientEmail = item.optString("clientEmail"),
                            category = LegalServiceCategory.valueOf(item.getString("category")),
                            mode = ConsultationMode.valueOf(item.getString("mode")),
                            preferredDate = item.getString("preferredDate"),
                            preferredTime = item.getString("preferredTime"),
                            note = item.optString("note"),
                            status = BookingStatus.valueOf(item.optString("status", BookingStatus.Draft.name)),
                            meetingUrl = item.optString("meetingUrl").takeIf { it.isNotBlank() },
                            contactConsentAtMillis = item.optLong("contactConsentAtMillis", 0L),
                            createdAtMillis = item.optLong("createdAtMillis", System.currentTimeMillis())
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    companion object {
        private const val KEY_REQUESTS = "requests"

        fun newDraft(
            clientName: String,
            mobileNumber: String,
            clientEmail: String,
            category: LegalServiceCategory,
            mode: ConsultationMode,
            preferredDate: String,
            preferredTime: String,
            note: String,
            contactConsentAtMillis: Long
        ) = LawyerBookingRequest(
            id = UUID.randomUUID().toString(),
            lawyerId = null,
            lawyerName = null,
            clientName = clientName.trim(),
            mobileNumber = mobileNumber.trim(),
            clientEmail = clientEmail.trim().lowercase(),
            category = category,
            mode = mode,
            preferredDate = preferredDate.trim(),
            preferredTime = preferredTime.trim(),
            note = note.trim(),
            status = BookingStatus.Draft,
            meetingUrl = null,
            contactConsentAtMillis = contactConsentAtMillis,
            createdAtMillis = System.currentTimeMillis()
        )
    }
}
