package com.maruf.bdtaxcalculator.firebase

import android.os.SystemClock
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray

data class FirebaseTaxNotice(
    val id: String,
    val banglaTitle: String,
    val englishTitle: String,
    val publishedDate: String,
    val url: String
)

data class FirebaseHomeNews(
    val isEnabled: Boolean,
    val banglaText: String,
    val englishText: String,
    val url: String,
    val notices: List<FirebaseTaxNotice>
)

object FirebaseHomeNewsStore {
    const val KeyEnabled = "home_news_enabled"
    const val KeyTextBangla = "home_news_text_bn"
    const val KeyTextEnglish = "home_news_text_en"
    const val KeyUrl = "home_news_url"
    const val KeyNoticesJson = "tax_notices_json"

    private const val FallbackBanglaText =
        "৩০ সেপ্টেম্বরের মধ্যে রিটার্ন দিন—পরিশোধযোগ্য করের ৫% ছাড়, সর্বোচ্চ ৳২৫,০০০।"
    private const val FallbackEnglishText =
        "File by 30 September—get 5% off tax payable, up to BDT 25,000."
    private const val FallbackUrl =
        "https://nbr.gov.bd/uploads/public-notice/Press_Release_Incentives.pdf"
    // Notices are not time-critical. A 12-hour production cache keeps each
    // installation to at most two backend fetches per day under normal use.
    private const val RefreshIntervalSeconds = 12 * 60 * 60L
    private const val RefreshIntervalMillis = RefreshIntervalSeconds * 1_000L
    private val fallbackNotices = listOf(
        FirebaseTaxNotice(
            id = "early_filing_incentive_2026",
            banglaTitle = "৩০ সেপ্টেম্বরের মধ্যে আয়কর রিটার্ন দাখিলে ৫% পর্যন্ত কর প্রণোদনা সংক্রান্ত প্রেস রিলিজ",
            englishTitle = "Press release on tax incentive for filing returns by 30 September",
            publishedDate = "02-08-2026",
            url = FallbackUrl
        ),
        FirebaseTaxNotice(
            id = "ereturn_launch_2026_27",
            banglaTitle = "ব্যক্তি শ্রেণির করদাতাদের জন্য ২০২৬-২০২৭ করবর্ষের ই-রিটার্ন চালু করল জাতীয় রাজস্ব বোর্ড",
            englishTitle = "NBR launches e-Return for individual taxpayers for tax year 2026-2027",
            publishedDate = "22-07-2026",
            url = "https://nbr.gov.bd/uploads/public-notice/eReturn_Press_Release-2026.pdf"
        ),
        FirebaseTaxNotice(
            id = "income_tax_act_section_147_2026",
            banglaTitle = "আয়কর আইন ২০২৩-এর ধারা ১৪৭ বিষয়ে সম্মানিত করদাতাদের জ্ঞাতব্য",
            englishTitle = "Notice to taxpayers regarding section 147 of the Income Tax Act 2023",
            publishedDate = "19-07-2026",
            url = "https://nbr.gov.bd/uploads/public-notice/press_19.jpeg"
        )
    )

    private val newsFlow = MutableStateFlow(fallbackNews())
    private var lastRefreshRequestMillis = 0L

    fun observe(): StateFlow<FirebaseHomeNews> = newsFlow

    @Synchronized
    fun refresh() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastRefreshRequestMillis < RefreshIntervalMillis) return
        lastRefreshRequestMillis = now

        val remoteConfig = FirebaseRemoteConfig.getInstance()
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(RefreshIntervalSeconds)
                .build()
        )

        remoteConfig
            .setDefaultsAsync(
                mapOf(
                    KeyEnabled to true,
                    KeyTextBangla to FallbackBanglaText,
                    KeyTextEnglish to FallbackEnglishText,
                    KeyUrl to FallbackUrl,
                    KeyNoticesJson to noticesToJson(fallbackNotices)
                )
            )
            .continueWithTask { remoteConfig.fetchAndActivate() }
            .addOnCompleteListener {
                newsFlow.value = remoteConfig.toHomeNews()
            }
    }

    private fun FirebaseRemoteConfig.toHomeNews(): FirebaseHomeNews {
        val banglaText = getString(KeyTextBangla).trim().ifEmpty { FallbackBanglaText }
        val englishText = getString(KeyTextEnglish).trim().ifEmpty { FallbackEnglishText }
        val configuredUrl = getString(KeyUrl).trim()
        val safeUrl = configuredUrl.takeIf { it.startsWith("https://") } ?: FallbackUrl

        return FirebaseHomeNews(
            isEnabled = getBoolean(KeyEnabled),
            banglaText = banglaText,
            englishText = englishText,
            url = safeUrl,
            notices = parseNotices(getString(KeyNoticesJson))
        )
    }

    private fun fallbackNews() = FirebaseHomeNews(
        isEnabled = true,
        banglaText = FallbackBanglaText,
        englishText = FallbackEnglishText,
        url = FallbackUrl,
        notices = fallbackNotices
    )

    private fun parseNotices(rawJson: String): List<FirebaseTaxNotice> {
        val parsed = runCatching {
            val jsonArray = JSONArray(rawJson)
            buildList {
                for (index in 0 until minOf(jsonArray.length(), 30)) {
                    val item = jsonArray.optJSONObject(index) ?: continue
                    if (!item.optBoolean("is_active", true)) continue

                    val id = item.optString("id").trim()
                    val banglaTitle = item.optString("title_bn").trim()
                    val englishTitle = item.optString("title_en").trim()
                    val publishedDate = item.optString("published_date").trim()
                    val url = item.optString("url").trim()
                    if (
                        id.isEmpty() ||
                        banglaTitle.isEmpty() ||
                        englishTitle.isEmpty() ||
                        publishedDate.isEmpty() ||
                        !url.startsWith("https://")
                    ) {
                        continue
                    }

                    add(
                        FirebaseTaxNotice(
                            id = id,
                            banglaTitle = banglaTitle,
                            englishTitle = englishTitle,
                            publishedDate = publishedDate,
                            url = url
                        )
                    )
                }
            }.distinctBy { it.id }
        }.getOrDefault(emptyList())

        return parsed.ifEmpty { fallbackNotices }
    }

    private fun noticesToJson(notices: List<FirebaseTaxNotice>): String {
        val jsonArray = JSONArray()
        notices.forEach { notice ->
            jsonArray.put(
                org.json.JSONObject()
                    .put("id", notice.id)
                    .put("title_bn", notice.banglaTitle)
                    .put("title_en", notice.englishTitle)
                    .put("published_date", notice.publishedDate)
                    .put("url", notice.url)
                    .put("is_active", true)
            )
        }
        return jsonArray.toString()
    }
}
