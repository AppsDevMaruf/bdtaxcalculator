package com.maruf.bdtaxcalculator.nbr

data class NbrTinLookupResult(
    val tin: String,
    val applicantName: String,
    val address: String,
    val taxZone: String,
    val taxCircle: String,
    val accountCode: String,
    val typeOfTin: String
)

class NbrTinLookupException(message: String, cause: Throwable? = null) : Exception(message, cause)
