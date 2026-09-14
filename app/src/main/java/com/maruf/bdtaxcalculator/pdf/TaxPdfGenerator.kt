package com.maruf.bdtaxcalculator.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.res.ResourcesCompat
import com.maruf.bdtaxcalculator.R
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TaxPdfGenerator {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val CONTENT_BOTTOM = 736f

    fun suggestedFileName(report: TaxPdfReport): String =
        "BD-Tax-Estimated-Report-${report.rules.assessmentYear.replace('-', '_')}.pdf"

    fun write(context: Context, destination: Uri, report: TaxPdfReport) {
        val document = PdfDocument()
        try {
            DocumentReportWriter(context, document, report).write()
            context.contentResolver.openOutputStream(destination, "w")?.use(document::writeTo)
                ?: error("Could not open the selected destination")
        } finally {
            document.close()
        }
    }

    fun write(context: Context, destination: File, report: TaxPdfReport) {
        destination.parentFile?.mkdirs()
        val document = PdfDocument()
        try {
            DocumentReportWriter(context, document, report).write()
            destination.outputStream().use(document::writeTo)
        } finally {
            document.close()
        }
    }

    /** Print-first A4 report with a compact promotional footer, not a banner. */
    private class DocumentReportWriter(
        context: Context,
        private val document: PdfDocument,
        private val report: TaxPdfReport
    ) {
        private val regular = if (report.isBangla) {
            ResourcesCompat.getFont(context, R.font.tiro_bangla_regular)
                ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        } else Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        private val bold = Typeface.create(regular, Typeface.BOLD)
        private val ink = Color.rgb(30, 35, 33)
        private val muted = Color.rgb(85, 91, 88)
        private val rule = Color.rgb(207, 212, 209)
        private val accent = Color.rgb(29, 92, 55)
        private val money = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
        private val generatedAt = formatGeneratedAt(Date())
        private val appQr = BitmapFactory.decodeResource(context.resources, R.drawable.tax_report_play_store_qr)
        private val appLogo = BitmapFactory.decodeResource(context.resources, R.drawable.tax_report_app_logo)
        private lateinit var page: PdfDocument.Page
        private lateinit var canvas: Canvas
        private var pageNumber = 0
        private var y = 0f
        private var currentSection = ""

        fun write() {
            newPage()
            section(label("করদাতার তথ্য", "Taxpayer information"))
            row(label("করদাতার শ্রেণী", "Taxpayer category"), taxpayerTypeLabel())
            row(label("করদাতার ধরন", "Assessment type"), assessmentTypeLabel())
            row(label("অবস্থান", "Location"), taxpayerLocationLabel())
            row(label("প্রতিবন্ধী নির্ভরশীলের সংখ্যা", "Disabled dependents"), localizedNumberText(report.disabledDependentCount.toString()))
            row(label("প্রযোজ্য করমুক্ত সীমা", "Applicable tax-free threshold"), amount(report.effectiveTaxFreeLimit))

            section(label("আয়ের বিবরণ", "Income statement"))
            row(label("বার্ষিক বেতন (মাসিক × ১২)", "Annual salary (monthly × 12)"), amount(report.salary.grossSalary * 12))
            row(label("বার্ষিক বোনাস", "Annual bonus"), amount(report.salary.yearlyBonus))
            row(label("অন্যান্য নিট করযোগ্য আয়", "Other net taxable income"), amount(report.salary.otherIncome))
            row(label("হিসাবে অন্তর্ভুক্ত মোট আয়", "Total income included in estimate"), amount(report.salary.totalIncome))
            row(label("বেতন আয়ের ছাড়", "Employment income exemption"), negativeAmount(report.salary.totalExemption))
            row(label("মোট করযোগ্য আয়", "Total taxable income"), amount(report.salary.taxableIncome), true)

            section(label("বিনিয়োগের বিবরণ", "Investment statement"))
            if (report.investments.isEmpty()) row(label("যোগ্য বিনিয়োগ দেওয়া হয়নি", "No eligible investment entered"), "-")
            report.investments.forEach { row(investmentTitle(it), amount(it.amount)) }
            row(label("মোট বিনিয়োগ", "Total investment"), amount(report.totalInvestment), true)

            section(label("কর হিসাব", "Tax computation"))
            report.result.breakdown.forEachIndexed { index, slab ->
                row(slabLabel(index, slab.rate) + " · " + amount(slab.amount), amount(slab.tax.toLong()))
            }
            row(label("মোট স্ল্যাব কর", "Gross slab tax"), amount(report.result.totalTax.toLong()), true)
            row(label("বিনিয়োগ রেয়াত", "Investment rebate"), negativeAmount(report.result.investmentRebate.toLong()))
            row(label("করদায় (প্রযোজ্য ন্যূনতম করসহ)", "Tax liability (including applicable minimum tax)"), amount(report.payment.taxLiability.toLong()), true)

            section(label("পরিশোধ ও সমন্বয়", "Payments and adjustments"))
            row(label("সমন্বয়যোগ্য উৎসে কর", "Adjustable tax deducted at source"), negativeAmount(report.payment.adjustableSourceTax.toLong()))
            row(label("পরিশোধিত অগ্রিম কর", "Advance tax paid"), negativeAmount(report.payment.advanceTax.toLong()))
            row(label("মোট সমন্বয়যোগ্য কর", "Total tax credit"), amount(report.payment.totalTaxCredit.toLong()))
            payableSummary()
            if (report.payment.excessPaid > 0) {
                row(label("অতিরিক্ত পরিশোধ / ক্রেডিট", "Excess paid / credit"), amount(report.payment.excessPaid.toLong()))
            }

            scopeNote(label(
                "প্রদত্ত তথ্যের ভিত্তিতে এই আনুমানিক হিসাব তৈরি করা হয়েছে। অন্যান্য আয়ের ঘরে সাধারণ স্ল্যাবে যোগযোগ্য, নিজে নির্ণীত নিট করযোগ্য আয় ধরা হয়েছে; উৎসভিত্তিক বিশেষ কর, খরচ বা ছাড় স্বয়ংক্রিয়ভাবে যাচাই করা হয়নি। রিটার্ন দাখিলের আগে প্রযোজ্য আইন ও সহায়ক কাগজপত্র যাচাই করুন।",
                "This estimate uses information entered by the user. Other income is treated as user-calculated net taxable income eligible for ordinary slabs; source-specific taxes, expenses and exemptions have not been verified automatically. Check applicable law and supporting documents before filing."
            ))
            finishPage()
        }

        private fun newPage() {
            pageNumber++
            page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
            canvas = page.canvas
            canvas.drawColor(Color.WHITE)
            drawText(label("আনুমানিক আয়কর প্রতিবেদন", "Estimated Income Tax Report"),
                40f, 34f, 515f, 19f, ink, bold)
            drawText(
                label("আয়বর্ষ", "Income year") + ": " + localizedNumberText(report.rules.incomeYear) +
                    "   |   " + label("করবর্ষ", "Assessment year") + ": " + localizedNumberText(report.rules.assessmentYear),
                40f, 64f, 515f, 9f, muted
            )
            drawText(label("তৈরির সময়", "Generated") + ": " + generatedAt, 40f, 82f, 515f, 9f, muted)
            horizontalRule(104f)
            y = 120f
        }

        private fun finishPage() {
            horizontalRule(750f)
            appLogo?.let { bitmap ->
                canvas.drawBitmap(bitmap, null, RectF(40f, 770f, 68f, 798f),
                    Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            }
            // Reserve a white quiet zone around the QR; keep all promotion in the footer.
            appQr?.let { bitmap ->
                canvas.drawBitmap(bitmap, null, RectF(480f, 762f, 528f, 810f), Paint().apply {
                    isFilterBitmap = false
                    isAntiAlias = false
                })
            }
            drawText("BD Tax Calculator", 76f, 771f, 215f, 9f, accent, bold)
            drawText(label("সহজ কর হিসাব · সমৃদ্ধ বাংলাদেশ",
                "Smart Tax · Strong Nation"),
                76f, 787f, 215f, 7f, muted)
            drawText(label("অ্যাপ পেতে স্ক্যান করুন", "Scan to get the app"),
                300f, 772f, 168f, 8f, accent, bold, Layout.Alignment.ALIGN_OPPOSITE)
            drawText("Smart tax calculation on Android",
                300f, 787f, 168f, 7f, muted, alignment = Layout.Alignment.ALIGN_OPPOSITE)
            drawText(localizedNumberText(pageNumber.toString()),
                537f, 779f, 18f, 8f, muted, alignment = Layout.Alignment.ALIGN_OPPOSITE)
            document.finishPage(page)
        }

        private fun ensureSpace(height: Float) {
            if (y + height <= CONTENT_BOTTOM) return
            finishPage()
            newPage()
            if (currentSection.isNotEmpty()) {
                heading(currentSection + label(" (চলমান)", " (continued)"))
            }
        }

        private fun section(title: String) {
            // Keep the heading and its first row on the same page.
            if (y + 75f > CONTENT_BOTTOM) {
                finishPage()
                newPage()
            }
            currentSection = title
            heading(title)
        }

        private fun heading(title: String) {
            y += 9f
            drawText(title, 40f, y, 515f, 12f, ink, bold)
            y += 23f
            horizontalRule(y)
            y += 6f
        }

        private fun row(title: String, value: String, strong: Boolean = false) {
            val face = if (strong) bold else regular
            val left = textLayout(title, 345, 10f, ink, face)
            val right = textLayout(value, 152, 10f, ink, face, Layout.Alignment.ALIGN_OPPOSITE)
            val height = maxOf(23f, maxOf(left.height, right.height) + 10f)
            ensureSpace(height)
            if (strong) {
                canvas.drawRect(40f, y - 2f, 555f, y + height - 2f,
                    Paint().apply { color = Color.rgb(244, 246, 244) })
            }
            drawLayout(left, 46f, y + 3f)
            drawLayout(right, 397f, y + 3f)
            y += height
            horizontalRule(y - 2f, 0.35f)
        }

        private fun payableSummary() {
            val title = textLayout(label("অবশিষ্ট প্রদেয় কর", "Remaining tax payable"), 285, 12f, accent, bold)
            val value = textLayout(amount(report.payment.remainingPayable.toLong()), 196, 17f,
                accent, bold, Layout.Alignment.ALIGN_OPPOSITE)
            val height = maxOf(48f, maxOf(title.height, value.height) + 22f)
            ensureSpace(height + 8f)
            y += 6f
            canvas.drawRect(40f, y, 555f, y + height, Paint().apply {
                color = Color.rgb(233, 245, 235)
            })
            canvas.drawRect(40f, y, 43f, y + height, Paint().apply { color = accent })
            drawLayout(title, 52f, y + (height - title.height) / 2f)
            drawLayout(value, 347f, y + (height - value.height) / 2f)
            y += height + 2f
        }

        private fun scopeNote(value: String) {
            val title = textLayout(label("হিসাবের সীমাবদ্ধতা", "Scope of this estimate"), 491, 10f, ink, bold)
            val body = textLayout(value, 491, 9f, muted, regular)
            val height = title.height + body.height + 32f
            // Keep the complete note together and visually separate from the tax table.
            if (y + height + 18f > CONTENT_BOTTOM) {
                finishPage()
                newPage()
            }
            currentSection = ""
            y += 18f
            canvas.drawRect(40f, y, 555f, y + height, Paint().apply {
                color = Color.rgb(248, 249, 248)
            })
            drawLayout(title, 52f, y + 10f)
            drawLayout(body, 52f, y + title.height + 20f)
            y += height
        }

        private fun horizontalRule(top: Float, width: Float = 0.6f) {
            canvas.drawLine(40f, top, 555f, top, Paint().apply {
                color = rule
                strokeWidth = width
            })
        }

        private fun textLayout(value: String, width: Int, size: Float, color: Int,
            face: Typeface, alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL): StaticLayout {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = size
                this.color = color
                typeface = face
            }
            return StaticLayout.Builder.obtain(value, 0, value.length, paint, width)
                .setAlignment(alignment).setIncludePad(false).setLineSpacing(2f, 1f).build()
        }

        private fun drawText(value: String, x: Float, top: Float, width: Float, size: Float,
            color: Int, face: Typeface = regular,
            alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL) {
            drawLayout(textLayout(value, width.toInt(), size, color, face, alignment), x, top)
        }

        private fun drawLayout(layout: StaticLayout, x: Float, top: Float) {
            canvas.save()
            canvas.translate(x, top)
            layout.draw(canvas)
            canvas.restore()
        }

        private fun amount(value: Long): String {
            val number = localizedNumberText(money.format(value.coerceAtLeast(0L)))
            return if (report.isBangla) "৳ $number" else "BDT $number"
        }

        private fun negativeAmount(value: Long): String = "- ${amount(value)}"

        private fun taxpayerTypeLabel(): String = when {
            report.taxpayerType.contains("Female", true) || report.taxpayerType.contains("মহিলা") -> label("মহিলা করদাতা", "Female Taxpayer")
            report.taxpayerType.contains("Senior", true) || report.taxpayerType.contains("সিনিয়র") -> label("সিনিয়র সিটিজেন", "Senior Citizen")
            report.taxpayerType.contains("Third", true) || report.taxpayerType.contains("তৃতীয়") -> label("তৃতীয় লিঙ্গ", "Third Gender")
            report.taxpayerType.contains("disability", true) || report.taxpayerType.contains("প্রতিবন্ধী") -> label("প্রতিবন্ধী", "Person with Disability")
            report.taxpayerType.contains("fighter", true) || report.taxpayerType.contains("মুক্তিযোদ্ধা") -> label("যুদ্ধাহত মুক্তিযোদ্ধা", "Freedom Fighter")
            else -> label("সাধারণ করদাতা", "General Taxpayer")
        }

        private fun assessmentTypeLabel(): String {
            return if (report.assessmentType.contains("New", true) || report.assessmentType.contains("নতুন")) {
                label("নতুন করদাতা", "New taxpayer")
            } else {
                label("বিদ্যমান করদাতা", "Existing taxpayer")
            }
        }

        private fun taxpayerLocationLabel(): String = when {
            report.taxpayerLocation.contains("Other", true) || report.taxpayerLocation.contains("অন্যান্য") -> label("অন্যান্য সিটি কর্পোরেশন", "Other City Corporation")
            report.taxpayerLocation.contains("Outside", true) || report.taxpayerLocation.contains("বাইরে") -> label("সিটি কর্পোরেশনের বাইরে", "Outside City Corporation")
            else -> label("ঢাকা উত্তর/দক্ষিণ বা চট্টগ্রাম সিটি", "Dhaka North/South or Chattogram City")
        }

        private fun taxpayerLocationShortLabel(): String = when {
            report.taxpayerLocation.contains("Other", true) || report.taxpayerLocation.contains("অন্যান্য") -> label("অন্যান্য সিটি", "Other City Corporation")
            report.taxpayerLocation.contains("Outside", true) || report.taxpayerLocation.contains("বাইরে") -> label("সিটির বাইরে", "Outside City Area")
            else -> label("ঢাকা/চট্টগ্রাম সিটি", "Dhaka/Chattogram City")
        }

        private fun investmentTitle(investment: TaxPdfInvestment): String {
            if (report.isBangla) {
                return when (investment.type) {
                    "gpf" -> "GPF-এ নিজস্ব জমা"
                    "recognized_pf" -> "স্বীকৃত ভবিষ্য তহবিল"
                    "benevolent_group_insurance" -> "কল্যাণ তহবিল / গোষ্ঠী বিমা"
                    "superannuation" -> "অনুমোদিত সুপারএনুয়েশন ফান্ড"
                    "universal_pension" -> "সর্বজনীন পেনশন স্কিম"
                    "insurance" -> "জীবন বিমা / ডেফার্ড অ্যানুইটি"
                    "dps" -> "DPS"
                    "sanchaypatra" -> "সরকারি সিকিউরিটিজ / সঞ্চয়পত্র"
                    "dse" -> "তালিকাভুক্ত শেয়ার / স্টক"
                    "mutual" -> "ইউনিট / মিউচুয়াল ফান্ড / ETF"
                    "zakat" -> "যাকাত তহবিলে দান"
                    "charitable_hospital" -> "অনুমোদিত দাতব্য হাসপাতালে দান"
                    "disability_welfare" -> "প্রতিবন্ধী কল্যাণ প্রতিষ্ঠানে দান"
                    "benevolent_education" -> "জনকল্যাণ / শিক্ষা প্রতিষ্ঠানে দান"
                    "liberation_war" -> "মুক্তিযুদ্ধ স্মৃতি সংরক্ষণে অনুদান"
                    "sro_approved_donation" -> "অন্যান্য SRO-অনুমোদিত দান"
                    else -> investment.title
                }
            }
            return when (investment.type) {
                "gpf" -> "GPF contribution"
                "recognized_pf" -> "Recognized provident fund"
                "benevolent_group_insurance" -> "Benevolent fund / group insurance"
                "superannuation" -> "Approved superannuation fund"
                "universal_pension" -> "Universal pension"
                "insurance" -> "Life insurance"
                "dps" -> "DPS"
                "sanchaypatra" -> "Savings certificates"
                "dse" -> "Listed shares / stocks"
                "mutual" -> "Mutual funds / ETF"
                "zakat" -> "Zakat Fund donation"
                "charitable_hospital" -> "Charitable hospital donation"
                "disability_welfare" -> "Disability welfare donation"
                "benevolent_education" -> "Education organization donation"
                "liberation_war" -> "Liberation War memorial donation"
                "sro_approved_donation" -> "Other SRO-approved donation"
                else -> investment.title
            }
        }


        private fun slabLabel(index: Int, rate: Double): String {
            val number = localizedNumberText((index + 1).toString())
            val percent = localizedNumberText(trimRate(rate))
            return if (report.isBangla) "ধাপ $number: $percent%" else "Slab $number: $percent%"
        }

        private fun trimRate(rate: Double): String = if (rate % 1.0 == 0.0) rate.toInt().toString() else rate.toString()

        private fun label(bangla: String, english: String): String = if (report.isBangla) bangla else english

        private fun localizedNumberText(value: String): String = if (report.isBangla) value.toBanglaDigits() else value

        private fun formatGeneratedAt(date: Date): String {
            if (!report.isBangla) return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH).format(date)
            val calendar = Calendar.getInstance().apply { time = date }
            val months = arrayOf(
                "জানুয়ারি",
                "ফেব্রুয়ারি",
                "মার্চ",
                "এপ্রিল",
                "মে",
                "জুন",
                "জুলাই",
                "আগস্ট",
                "সেপ্টেম্বর",
                "অক্টোবর",
                "নভেম্বর",
                "ডিসেম্বর"
            )
            val hour = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
            val minute = calendar.get(Calendar.MINUTE).toString().padStart(2, '0')
            val period = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "এএম" else "পিএম"
            return "${localizedNumberText(calendar.get(Calendar.DAY_OF_MONTH).toString())} ${months[calendar.get(Calendar.MONTH)]} " +
                "${localizedNumberText(calendar.get(Calendar.YEAR).toString())}, ${localizedNumberText(hour.toString())}:${localizedNumberText(minute)} $period"
        }

        private fun String.toBanglaDigits(): String {
            val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
            return map { char ->
                if (char in '0'..'9') banglaDigits[char - '0'] else char
            }.joinToString("")
        }


    }
}
