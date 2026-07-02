package com.maruf.bdtaxcalculator.nbr

import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object NbrTinLookupRepository {
    private const val PAYMENT_URL = "https://nbr.sblesheba.com/IncomeTax/Payment"
    private const val REQUEST_TOKEN_NAME = "__RequestVerificationToken"
    private const val CONNECT_TIMEOUT_MS = 15_000
    private const val READ_TIMEOUT_MS = 20_000
    fun lookup(tin: String): NbrTinLookupResult {
        val normalizedTin = tin.normalizeTinDigits()
        if (normalizedTin.length != 12) {
            throw NbrTinLookupException("TIN must be 12 digits.")
        }

        val getConnection = openConnection("GET")
        val initialPage = try {
            getConnection.connect()
            PageResponse(
                html = getConnection.readTextResponse(),
                cookies = getConnection.headerFields["Set-Cookie"].orEmpty().toCookieHeader()
            )
        } finally {
            getConnection.disconnect()
        }

        val formToken = initialPage.html.extractRequestVerificationToken()
            ?: throw NbrTinLookupException("Could not start a secure NBR lookup session.")
        val cookieHeader = initialPage.cookies
            .takeIf(String::isNotBlank)
            ?: throw NbrTinLookupException("Could not read NBR session cookie.")

        val postConnection = openConnection("POST")
        val responseHtml = try {
            val body = buildSearchBody(formToken = formToken, tin = normalizedTin)
            postConnection.setRequestProperty("Cookie", cookieHeader)
            postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            postConnection.setRequestProperty("Origin", "https://nbr.sblesheba.com")
            postConnection.setRequestProperty("Referer", PAYMENT_URL)
            postConnection.doOutput = true

            OutputStreamWriter(postConnection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(body)
            }
            postConnection.readTextResponse()
        } finally {
            postConnection.disconnect()
        }

        return responseHtml.toTinLookupResult(normalizedTin)
    }
    private fun openConnection(method: String): HttpURLConnection {
        return (URL(PAYMENT_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            instanceFollowRedirects = true
            setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            setRequestProperty("Accept-Language", "en-US,en;q=0.9,bn;q=0.8")
            setRequestProperty("Cache-Control", "no-cache")
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android) AppleWebKit/537.36 (KHTML, like Gecko) Chrome Mobile Safari/537.36"
            )
        }
    }
    private fun buildSearchBody(formToken: String, tin: String): String {
        return listOf(
            REQUEST_TOKEN_NAME to formToken,
            "TIN" to tin,
            "btn" to "Search",
            "ApplicantName" to "",
            "Address" to "",
            "TaxZone" to "",
            "ZoneNo" to "",
            "TaxCircle" to "",
            "CircleNo" to "",
            "AccountCode" to "",
            // The official form contains both select and hidden inputs with these names.
            // Sending both keeps our request shape close to a browser form submit.
            "PaymentTypeCode" to "",
            "PaymentTypeCode" to "",
            "PaymentUnderSection" to "",
            "AssessmentYear" to "2025-26",
            "AssessmentYear" to "",
            "Amount" to "0.00",
            "TypeOfTin" to "",
            "MobileNo" to "",
            "Remarks" to ""
        ).joinToString("&") { (key, value) ->
            "${key.urlEncode()}=${value.urlEncode()}"
        }
    }
    private fun String.toTinLookupResult(tin: String): NbrTinLookupResult {
        val result = NbrTinLookupResult(
            tin = readFormField("TIN").ifBlank { tin },
            applicantName = readFormField("ApplicantName"),
            address = readFormField("Address"),
            taxZone = readFormField("TaxZone"),
            taxCircle = readFormField("TaxCircle"),
            accountCode = readFormField("AccountCode"),
            typeOfTin = readFormField("TypeOfTin")
        )

        if (result.applicantName.isBlank() || result.accountCode.isBlank()) {
            throw NbrTinLookupException("No taxpayer information was found for this TIN.")
        }

        return result
    }
    private fun String.extractRequestVerificationToken(): String? {
        return readFormField(REQUEST_TOKEN_NAME).takeIf(String::isNotBlank)
    }
    private fun String.readFormField(fieldName: String): String {
        val inputMatch = Regex(
            pattern = "<input\\b[^>]*(?:name|id)\\s*=\\s*['\"]${Regex.escape(fieldName)}['\"][^>]*>",
            options = setOf(RegexOption.IGNORE_CASE)
        ).find(this)
        val inputTag = inputMatch?.value
        if (inputTag != null) {
            return inputTag.readAttribute("value").htmlDecode().cleanField()
        }

        val textareaMatch = Regex(
            pattern = "<textarea\\b[^>]*(?:name|id)\\s*=\\s*['\"]${Regex.escape(fieldName)}['\"][^>]*>(.*?)</textarea>",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(this)
        return textareaMatch?.groupValues?.getOrNull(1).orEmpty().htmlDecode().cleanField()
    }
    private fun String.readAttribute(attributeName: String): String {
        return Regex(
            pattern = "\\b${Regex.escape(attributeName)}\\s*=\\s*(['\"])(.*?)\\1",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        ).find(this)?.groupValues?.getOrNull(2).orEmpty()
    }
    private fun HttpURLConnection.readTextResponse(): String {
        val code = responseCode
        val stream = if (code in 200..399) inputStream else errorStream
        val body = stream?.bufferedReader(Charsets.UTF_8)?.use(BufferedReader::readText).orEmpty()
        if (code !in 200..399) {
            throw NbrTinLookupException("NBR server returned HTTP $code.")
        }
        return body
    }
    private fun List<String>.toCookieHeader(): String {
        return mapNotNull { cookie ->
            cookie.substringBefore(";").takeIf { it.contains("=") }
        }.joinToString("; ")
    }
    private fun String.urlEncode(): String {
        return URLEncoder.encode(this, "UTF-8")
    }
    private fun String.cleanField(): String {
        return replace(Regex("\\s+"), " ").trim()
    }
    private fun String.normalizeTinDigits(): String {
        return buildString {
            this@normalizeTinDigits.forEach { char ->
                when (char) {
                    in '0'..'9' -> append(char)
                    in '০'..'৯' -> append(('0'.code + char.code - '০'.code).toChar())
                    in '٠'..'٩' -> append(('0'.code + char.code - '٠'.code).toChar())
                }
            }
        }
    }
    private fun String.htmlDecode(): String {
        return replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("&#(\\d+);")) { match ->
                match.groupValues[1].toIntOrNull()?.toChar()?.toString().orEmpty()
            }
            .replace(Regex("&#x([0-9a-fA-F]+);")) { match ->
                match.groupValues[1].toIntOrNull(16)?.toChar()?.toString().orEmpty()
            }
    }
    private data class PageResponse(
        val html: String,
        val cookies: String
    )
}
