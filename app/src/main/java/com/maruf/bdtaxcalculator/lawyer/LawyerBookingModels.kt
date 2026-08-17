package com.maruf.bdtaxcalculator.lawyer

enum class LegalServiceCategory(val banglaLabel: String, val englishLabel: String) {
    IncomeTax("আয়কর", "Income Tax"),
    Vat("ভ্যাট", "VAT"),
    CompanyLaw("কোম্পানি আইন", "Company Law")
}

enum class ConsultationMode(val banglaLabel: String, val englishLabel: String) {
    Phone("ফোন", "Phone"),
    Online("অনলাইন", "Online"),
    Chamber("চেম্বার", "Chamber")
}

enum class BookingStatus(val banglaLabel: String, val englishLabel: String) {
    Draft("খসড়া", "Draft"),
    Pending("অপেক্ষমাণ", "Pending"),
    Accepted("গৃহীত", "Accepted"),
    Confirmed("নিশ্চিত", "Confirmed"),
    Started("চলমান", "In progress"),
    Completed("সম্পন্ন", "Completed"),
    Cancelled("বাতিল", "Cancelled"),
    NoShow("অনুপস্থিত", "No show"),
    UnderReview("পর্যালোচনায়", "Under review")
}

data class LawyerProfile(
    val id: String,
    val name: String,
    val barCouncilEnrollmentNumber: String,
    val barAssociation: String,
    val categories: Set<LegalServiceCategory>,
    val experienceYears: Int,
    val chamberAddress: String,
    val consultationFee: Int,
    val isManuallyVerified: Boolean
)

data class LawyerBookingRequest(
    val id: String,
    val lawyerId: String?,
    val lawyerName: String?,
    val clientName: String,
    val mobileNumber: String,
    val clientEmail: String,
    val category: LegalServiceCategory,
    val mode: ConsultationMode,
    val preferredDate: String,
    val preferredTime: String,
    val note: String,
    val status: BookingStatus,
    val meetingUrl: String?,
    val contactConsentAtMillis: Long,
    val createdAtMillis: Long
)
