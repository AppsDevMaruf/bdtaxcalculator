package com.maruf.bdtaxcalculator.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
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
    private const val PAGE_HEIGHT = 769

    fun suggestedFileName(report: TaxPdfReport): String =
        "BD-Tax-Estimated-Report-${report.rules.assessmentYear.replace('-', '_')}.pdf"

    fun write(context: Context, destination: Uri, report: TaxPdfReport) {
        val document = PdfDocument()
        try {
            ReferenceReportWriter(context, document, report).write()
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
            ReferenceReportWriter(context, document, report).write()
            destination.outputStream().use(document::writeTo)
        } finally {
            document.close()
        }
    }

    private class ReferenceReportWriter(
        context: Context,
        private val document: PdfDocument,
        private val report: TaxPdfReport
    ) {
        private val quicksandRegular = ResourcesCompat.getFont(context, R.font.quicksand_regular)
            ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        private val quicksandMedium = ResourcesCompat.getFont(context, R.font.quicksand_medium)
            ?: Typeface.create(quicksandRegular, Typeface.NORMAL)
        private val quicksandBold = ResourcesCompat.getFont(context, R.font.quicksand_bold)
            ?: Typeface.create(quicksandRegular, Typeface.BOLD)
        private val banglaRegular = ResourcesCompat.getFont(context, R.font.tiro_bangla_regular)
        private val regular = if (report.isBangla) banglaRegular ?: quicksandRegular else quicksandRegular
        private val medium = if (report.isBangla) Typeface.create(regular, Typeface.BOLD) else quicksandMedium
        private val bold = if (report.isBangla) Typeface.create(regular, Typeface.BOLD) else quicksandBold
        private val money = NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 0 }
        private val now = Date()
        private val generatedAt = formatGeneratedAt(now)
        private val reference = "TAX-${localizedNumberText(report.rules.assessmentYear)}-${localizedNumberText(SimpleDateFormat("MMddyyyy", Locale.US).format(now))}"
        private val logoBitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, R.drawable.tax_report_app_logo)
        private val qrBitmap: Bitmap? = BitmapFactory.decodeResource(context.resources, R.drawable.tax_report_play_store_qr)

        private val primary = Color.rgb(11, 106, 58)
        private val dark = Color.rgb(5, 77, 43)
        private val secondary = Color.rgb(22, 163, 74)
        private val tertiary = Color.rgb(34, 197, 94)
        private val background = Color.rgb(246, 251, 247)
        private val paleGreen = Color.rgb(237, 249, 237)
        private val border = Color.rgb(180, 214, 180)
        private val ink = Color.rgb(24, 40, 29)
        private val muted = Color.rgb(63, 76, 67)
        private val danger = Color.rgb(229, 57, 53)
        private val paleRed = Color.rgb(255, 241, 241)

        fun write() {
            val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create())
            drawReferencePage(page.canvas)
            document.finishPage(page)
            if (report.investments.size > 3) drawInvestmentAppendix()
        }

        private fun drawReferencePage(canvas: Canvas) {
            canvas.drawColor(background)
            drawHeroHeader(canvas)
            drawPayablePanel(canvas)
            drawStatusChips(canvas)
            drawProfileCard(canvas)
            drawIncomeCard(canvas)
            drawTaxCard(canvas)
            drawInvestmentCard(canvas)
            drawPaymentCard(canvas)
            drawInstallBanner(canvas)
            drawDisclaimer(canvas)
            strokeRound(canvas, 0.5f, 0.5f, 594f, PAGE_HEIGHT - 0.5f, 8f, Color.rgb(211, 221, 207), 0.8f)
        }

        private fun drawHeroHeader(canvas: Canvas) {
            val gradient = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(0f, 0f, PAGE_WIDTH.toFloat(), 116f, dark, primary, Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 120f, gradient)
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(42, 107, 221, 111)
                val wave = Path().apply {
                    moveTo(135f, 0f)
                    cubicTo(240f, 45f, 310f, 12f, 430f, 0f)
                    lineTo(360f, 72f)
                    cubicTo(285f, 113f, 165f, 120f, 95f, 92f)
                    close()
                }
                canvas.drawPath(wave, this)
            }
            dotPattern(canvas, 558f, 18f, 4, 4, 7f, Color.argb(100, 112, 210, 83))
            fillRound(canvas, -7f, -9f, 112f, 108f, 32f, Color.WHITE)
            drawBrandMark(canvas, 18f, 18f, 78f, showTagline = true)
            text(canvas, label("বিডি ট্যাক্স ক্যালকুলেটর", "BD Tax Calculator"), 120f, 23f, 305f, titleSize(28f), Color.WHITE, bold)
            text(canvas, label("স্মার্ট আয়কর হিসাব প্রতিবেদন", "Smart Income Tax Estimation Report"), 121f, 57f, 300f, bodySize(14f), Color.WHITE, medium)
            fillRound(canvas, 120f, 82f, 228f, 99f, 9f, Color.argb(95, 138, 225, 129))
            text(canvas, label("আনুমানিক রিপোর্ট", "ESTIMATED REPORT"), 130f, 85f, 90f, badgeSize(8f), Color.WHITE, bold)
            drawGrowthIllustration(canvas, 350f, 18f)
            drawTaxDocument(canvas, 483f, 21f)
        }

        private fun drawPayablePanel(canvas: Canvas) {
            shadowCard(canvas, 25f, 107f, 570f, 228f, 14f)
            text(canvas, label("পরিশোধযোগ্য অবশিষ্ট কর", "REMAINING TAX PAYABLE"), 45f, 123f, 245f, headingSize(12f), dark, bold)
            text(canvas, amount(report.payment.remainingPayable.toLong()), 44f, 145f, 270f, titleSize(30f), danger, bold)
            drawSmallDocumentIcon(canvas, 46f, 188f, dark)
            text(canvas, "${label("রেফারেন্স", "Reference")}:  $reference", 65f, 186f, 238f, bodySize(9f), ink, regular)
            drawCalendarIcon(canvas, 46f, 207f, dark)
            text(canvas, "${label("তৈরি", "Generated")}:  $generatedAt", 65f, 205f, 238f, bodySize(9f), ink, regular)
            line(canvas, 310f, 126f, 310f, 215f, Color.rgb(175, 214, 175), 0.8f)
            strokeRound(canvas, 329f, 127f, 554f, 214f, 12f, secondary, 0.9f)
            fillRound(canvas, 338f, 140f, 397f, 201f, 10f, Color.WHITE)
            strokeRound(canvas, 338f, 140f, 397f, 201f, 10f, primary, 1.4f)
            drawBrandMark(canvas, 346f, 151f, 43f, showTagline = false)
            text(canvas, label("অ্যাপ নিন", "Get the app"), 406f, 146f, 75f, headingSize(11f), dark, bold)
            text(canvas, label("দ্রুত কর রিপোর্ট", "Quick tax report"), 406f, 168f, 85f, bodySize(8f), muted, regular)
            text(canvas, label("স্ক্যান করে ইনস্টল", "Scan to install"), 406f, 188f, 78f, bodySize(8f), primary, bold)
            drawQr(canvas, 481f, 136f, 64f)
        }

        private fun drawStatusChips(canvas: Canvas) {
            statusChip(canvas, 26f, 237f, 210f, "calendar", label("করবর্ষ", "Assessment Year") + " ${localizedNumberText(report.rules.assessmentYear)}")
            statusChip(canvas, 219f, 237f, 378f, "person", taxpayerTypeLabel())
            statusChip(canvas, 388f, 237f, 569f, "location", taxpayerLocationShortLabel())
        }

        private fun drawProfileCard(canvas: Canvas) {
            sectionCard(canvas, 25f, 271f, 294f, 397f, "profile", label("করদাতার প্রোফাইল", "TAXPAYER PROFILE"))
            var y = 313f
            val rows = listOf(
                label("ধরন", "Category") to taxpayerTypeLabel(),
                label("অ্যাসেসমেন্ট ধরন", "Assessment type") to assessmentTypeLabel(),
                label("অবস্থান", "Location") to taxpayerLocationLabel(),
                label("আয়বর্ষ", "Income year") to localizedNumberText(report.rules.incomeYear),
                label("করমুক্ত সীমা", "Threshold") to amount(report.effectiveTaxFreeLimit)
            )
            rows.forEachIndexed { index, row ->
                keyValue(canvas, row.first, row.second, 37f, y, 244f, index == rows.lastIndex, if (index == rows.lastIndex) secondary else ink)
                y += 17f
            }
        }

        private fun drawIncomeCard(canvas: Canvas) {
            sectionCard(canvas, 301f, 271f, 570f, 397f, "bars", label("আয়ের সারসংক্ষেপ", "INCOME SUMMARY"))
            val rows = listOf(
                Triple(label("মাসিক বেতন", "Monthly salary"), amount(report.salary.grossSalary), ink),
                Triple(label("বার্ষিক বোনাস", "Yearly bonus"), amount(report.salary.yearlyBonus), ink),
                Triple(label("বার্ষিক আয়", "Annual income"), amount(report.salary.totalIncome), secondary),
                Triple(label("বেতন ছাড়", "Salary exemption"), negativeAmount(report.salary.totalExemption), danger)
            )
            var y = 313f
            rows.forEach { row ->
                keyValue(canvas, row.first, row.second, 313f, y, 244f, row.third != ink, row.third)
                y += 17f
            }
            line(canvas, 313f, 379f, 557f, 379f, Color.rgb(113, 170, 117), 0.6f)
            keyValue(canvas, label("নেট করযোগ্য আয়", "Net taxable income"), amount(report.salary.taxableIncome), 313f, 386f, 244f, true, danger)
        }

        private fun drawTaxCard(canvas: Canvas) {
            sectionCard(canvas, 25f, 404f, 294f, 526f, "calculator", label("কর হিসাব", "TAX CALCULATION"))
            val x = 37f
            val valueX = 168f
            val rowHeight = if (report.result.breakdown.size >= 5) 8.2f else 10f
            var y = 446f
            report.result.breakdown.forEachIndexed { index, slab ->
                text(canvas, slabLabel(index, slab.rate), x, y, 110f, bodySize(7.2f), muted, regular)
                text(canvas, amount(slab.tax.toLong()), valueX, y, 100f, bodySize(7.2f), ink, medium, Layout.Alignment.ALIGN_OPPOSITE)
                y += rowHeight
            }
            line(canvas, x, y + 1f, 281f, y + 1f, Color.rgb(119, 174, 122), 0.55f)
            y += 5f
            compactPair(canvas, label("মোট স্ল্যাব কর", "Gross slab tax"), amount(report.result.totalTax.toLong()), x, y, secondary)
            y += 10f
            compactPair(canvas, label("বিনিয়োগ রেয়াত", "Investment rebate"), negativeAmount(report.result.investmentRebate.toLong()), x, y, danger)
            y += 10f
            line(canvas, x, y, 281f, y, Color.rgb(119, 174, 122), 0.55f)
            y += 5f
            compactPair(canvas, label("রেয়াতের পরে", "After rebate"), amount(report.result.taxAfterRebate.toLong()), x, y, danger, strong = true)
        }

        private fun drawInvestmentCard(canvas: Canvas) {
            sectionCard(canvas, 301f, 404f, 570f, 526f, "shield", label("বিনিয়োগ বিবরণ", "INVESTMENT DETAILS"))
            var y = 446f
            val visible = report.investments.take(3)
            if (visible.isEmpty()) {
                text(canvas, label("যোগ্য বিনিয়োগ দেওয়া হয়নি", "No eligible investment entered"), 313f, y, 240f, bodySize(7.5f), muted, regular)
                y += 13f
            } else {
                visible.forEach {
                    keyValue(canvas, investmentTitle(it), amount(it.amount), 313f, y, 244f)
                    y += 15f
                }
            }
            if (report.investments.size > visible.size) {
                text(canvas, moreInvestmentsLabel(report.investments.size - visible.size), 313f, y, 240f, bodySize(7f), secondary, medium)
            }
            line(canvas, 313f, 481f, 557f, 481f, Color.rgb(119, 174, 122), 0.55f)
            keyValue(canvas, label("মোট বিনিয়োগ", "Total investment"), amount(report.totalInvestment), 313f, 488f, 244f, true, secondary)
            val trackY = 508f
            fillRound(canvas, 313f, trackY, 557f, trackY + 5f, 3f, Color.rgb(219, 226, 215))
            val eligibleBase = report.salary.taxableIncome.coerceAtLeast(1L).toFloat()
            val progress = (report.totalInvestment / eligibleBase).coerceIn(0f, 1f)
            fillRound(canvas, 313f, trackY, 313f + 244f * progress, trackY + 5f, 3f, secondary)
            text(canvas, "${label("রেয়াত প্রয়োগ", "Rebate applied")}: ${amount(report.result.investmentRebate.toLong())}", 313f, 516f, 244f, bodySize(7.2f), secondary, bold)
        }

        private fun drawPaymentCard(canvas: Canvas) {
            sectionCard(canvas, 25f, 533f, 570f, 622f, "payment", label("পরিশোধ ও সমন্বয়", "PAYMENT AND ADJUSTMENT"))
            paymentTile(canvas, 40f, 574f, 119f, 617f, label("কর দায়", "Tax liability"), amount(report.payment.taxLiability.toLong()), secondary, "document")
            equationSymbol(canvas, "-", 130f, 591f)
            paymentTile(canvas, 151f, 574f, 231f, 617f, label("উৎসে কর", "Source tax"), negativeAmount(report.payment.adjustableSourceTax.toLong()), secondary, "down")
            equationSymbol(canvas, "-", 241f, 591f)
            paymentTile(canvas, 258f, 574f, 338f, 617f, label("অগ্রিম কর", "Advance tax"), negativeAmount(report.payment.advanceTax.toLong()), secondary, "down")
            equationSymbol(canvas, "+", 347f, 591f)
            paymentTile(canvas, 363f, 574f, 443f, 617f, label("কর ক্রেডিট", "Tax credit"), amount(report.payment.totalTaxCredit.toLong()), secondary, "shield")
            equationSymbol(canvas, "=", 451f, 591f)
            paymentTile(canvas, 468f, 574f, 552f, 617f, label("পরিশোধযোগ্য", "Payable"), amount(report.payment.remainingPayable.toLong()), danger, "card")
        }

        private fun drawInstallBanner(canvas: Canvas) {
            val gradient = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(25f, 0f, 570f, 0f, secondary, dark, Shader.TileMode.CLAMP)
            }
            canvas.drawRoundRect(RectF(25f, 630f, 570f, 704f), 10f, 10f, gradient)
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(30, 180, 255, 190)
                val wave = Path().apply {
                    moveTo(190f, 630f); cubicTo(310f, 655f, 370f, 628f, 500f, 631f)
                    lineTo(460f, 680f); cubicTo(330f, 674f, 250f, 695f, 150f, 660f); close()
                }
                canvas.drawPath(wave, this)
            }
            fillRound(canvas, 39f, 639f, 99f, 696f, 9f, Color.WHITE)
            drawBrandMark(canvas, 48f, 648f, 42f, showTagline = false)
            text(canvas, label("BD Tax Calculator অ্যাপ", "BD Tax Calculator App"), 115f, 640f, 250f, titleSize(17f), Color.WHITE, bold)
            text(canvas, label("আপনার পকেটে সহজ ও নিরাপদ কর হিসাব।", "Smart, simple & secure tax estimation in your pocket."), 116f, 664f, 255f, bodySize(8.5f), Color.WHITE, regular)
            featureBadge(canvas, 115f, 681f, label("অফলাইন", "Offline friendly"))
            featureBadge(canvas, 181f, 681f, label("PDF রিপোর্ট", "PDF report"))
            featureBadge(canvas, 247f, 681f, label("রেয়াত চেক", "Rebate check"))
            featureBadge(canvas, 317f, 681f, label("দ্রুত হিসাব", "Fast calculation"))
            line(canvas, 404f, 640f, 404f, 694f, Color.argb(170, 255, 255, 255), 0.7f)
            text(canvas, label("স্ক্যান করুন", "Scan & install"), 415f, 646f, 88f, headingSize(10.5f), Color.WHITE, bold)
            text(canvas, label("স্মার্ট কর হিসাবের\nজন্য অ্যাপ নিন", "Get the app now\nfor smart tax\ncalculations"), 415f, 664f, 78f, bodySize(7.2f), Color.WHITE, regular)
            fillRound(canvas, 493f, 637f, 558f, 697f, 6f, Color.WHITE)
            drawQr(canvas, 499f, 642f, 53f)
        }

        private fun drawDisclaimer(canvas: Canvas) {
            fillRound(canvas, 25f, 713f, 570f, 756f, 9f, Color.rgb(252, 255, 249))
            strokeRound(canvas, 25f, 713f, 570f, 756f, 9f, border, 0.65f)
            drawShieldIcon(canvas, 43f, 721f, 25f, secondary)
            text(canvas, label("নোট:", "Note:"), 80f, 724f, 40f, bodySize(8f), dark, bold)
            text(
                canvas,
                label(
                    "এটি আপনার দেওয়া তথ্যের ভিত্তিতে আনুমানিক কর হিসাব, অফিসিয়াল NBR রিটার্ন, চালান, সনদ বা পেমেন্ট প্রমাণ নয়। ফাইল করার আগে প্রযোজ্য আইন ও সহায়ক কাগজপত্র যাচাই করুন।",
                    "This is an estimated calculation report based on user-entered information, not an official NBR return, challan, certificate, or proof of payment. Verify applicable law and supporting documents before filing."
                ),
                113f,
                724f,
                430f,
                bodySize(7.1f),
                ink,
                regular,
                maxLines = 4
            )
        }

        private fun drawInvestmentAppendix() {
            val page = document.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 2).create())
            val canvas = page.canvas
            canvas.drawColor(background)
            val header = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(0f, 0f, PAGE_WIDTH.toFloat(), 105f, dark, primary, Shader.TileMode.CLAMP)
            }
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 105f, header)
            fillRound(canvas, 26f, 18f, 88f, 82f, 12f, Color.WHITE)
            drawBrandMark(canvas, 34f, 28f, 46f, showTagline = false)
            text(canvas, label("বিনিয়োগ বিবরণ", "Investment Details"), 108f, 26f, 320f, titleSize(23f), Color.WHITE, bold)
            text(canvas, "${label("ধারাবাহিকতা", "Continuation of")} $reference", 109f, 60f, 320f, bodySize(9f), Color.WHITE, regular)
            text(canvas, label("পৃষ্ঠা ২", "Page 2"), 505f, 48f, 55f, bodySize(9f), Color.WHITE, medium, Layout.Alignment.ALIGN_OPPOSITE)
            sectionCard(canvas, 25f, 125f, 570f, 684f, "shield", label("সব যোগ্য বিনিয়োগ", "ALL ELIGIBLE INVESTMENTS"))
            var y = 172f
            report.investments.forEachIndexed { index, investment ->
                if (index % 2 == 0) fillRound(canvas, 38f, y - 4f, 557f, y + 19f, 4f, Color.rgb(245, 251, 245))
                text(canvas, "${localizedNumberText((index + 1).toString())}.", 47f, y, 22f, bodySize(8f), secondary, bold)
                text(canvas, investmentTitle(investment), 70f, y, 330f, bodySize(8f), ink, regular, maxLines = 2)
                text(canvas, amount(investment.amount), 424f, y, 120f, bodySize(8f), ink, medium, Layout.Alignment.ALIGN_OPPOSITE)
                y += 28f
            }
            line(canvas, 38f, y, 557f, y, tertiary, 0.8f)
            text(canvas, label("মোট বিনিয়োগ", "TOTAL INVESTMENT"), 47f, y + 14f, 230f, bodySize(9f), dark, bold)
            text(canvas, amount(report.totalInvestment), 405f, y + 14f, 139f, bodySize(10f), secondary, bold, Layout.Alignment.ALIGN_OPPOSITE)
            text(canvas, label("পৃষ্ঠা ২ / ২", "Page 2 of 2"), 480f, 742f, 75f, bodySize(8f), muted, medium, Layout.Alignment.ALIGN_OPPOSITE)
            document.finishPage(page)
        }

        private fun sectionCard(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, icon: String, title: String) {
            shadowCard(canvas, left, top, right, bottom, 10f)
            fillRound(canvas, left + 12f, top + 8f, left + 33f, top + 29f, 5f, secondary)
            drawSectionIcon(canvas, icon, left + 16f, top + 12f, Color.WHITE)
            text(canvas, title, left + 42f, top + 11f, right - left - 55f, headingSize(11f), dark, bold)
            line(canvas, left + 12f, top + 33f, right - 14f, top + 33f, primary, 0.75f)
        }

        private fun statusChip(canvas: Canvas, left: Float, top: Float, right: Float, icon: String, label: String) {
            fillRound(canvas, left, top, right, top + 27f, 14f, Color.WHITE)
            strokeRound(canvas, left, top, right, top + 27f, 14f, Color.rgb(94, 167, 92), 0.7f)
            drawSectionIcon(canvas, icon, left + 18f, top + 7f, secondary)
            text(canvas, label, left + 47f, top + 7f, right - left - 58f, bodySize(8.2f), dark, bold, maxLines = 1)
        }

        private fun paymentTile(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, label: String, value: String, color: Int, icon: String) {
            fillRound(canvas, left, top, right, bottom, 7f, if (color == danger) paleRed else Color.rgb(248, 253, 246))
            strokeRound(canvas, left, top, right, bottom, 7f, if (color == danger) Color.rgb(250, 135, 135) else border, 0.65f)
            fillRound(canvas, (left + right) / 2f - 10f, top - 7f, (left + right) / 2f + 10f, top + 13f, 10f, Color.WHITE)
            strokeRound(canvas, (left + right) / 2f - 10f, top - 7f, (left + right) / 2f + 10f, top + 13f, 10f, if (color == danger) Color.rgb(250, 135, 135) else border, 0.5f)
            drawSectionIcon(canvas, icon, (left + right) / 2f - 5f, top - 2f, color)
            text(canvas, label, left + 4f, top + 18f, right - left - 8f, bodySize(6.8f), ink, regular, Layout.Alignment.ALIGN_CENTER, 1)
            text(canvas, value, left + 3f, top + 31f, right - left - 6f, bodySize(7.2f), color, bold, Layout.Alignment.ALIGN_CENTER, 1)
        }

        private fun keyValue(canvas: Canvas, label: String, value: String, x: Float, y: Float, width: Float, strong: Boolean = false, valueColor: Int = ink) {
            text(canvas, label, x, y, width * 0.48f, bodySize(7.3f), muted, regular, maxLines = 1)
            text(canvas, value, x + width * 0.46f, y, width * 0.54f, bodySize(7.3f), valueColor, if (strong) bold else medium, Layout.Alignment.ALIGN_OPPOSITE, 1)
        }

        private fun compactPair(canvas: Canvas, label: String, value: String, x: Float, y: Float, valueColor: Int, strong: Boolean = false) {
            text(canvas, label, x, y, 112f, bodySize(7f), muted, if (strong) medium else regular, maxLines = 1)
            text(canvas, value, x + 108f, y, 136f, bodySize(7.2f), valueColor, if (strong) bold else medium, Layout.Alignment.ALIGN_OPPOSITE, 1)
        }

        private fun featureBadge(canvas: Canvas, x: Float, y: Float, label: String) {
            val width = textWidth(label, badgeSize(6f), medium) + 18f
            fillRound(canvas, x, y, x + width, y + 14f, 7f, Color.argb(46, 255, 255, 255))
            fillRound(canvas, x + 5f, y + 5f, x + 9f, y + 9f, 2f, Color.WHITE)
            text(canvas, label, x + 12f, y + 3f, width - 14f, badgeSize(6f), Color.WHITE, medium, maxLines = 1)
        }

        private fun drawBrandMark(canvas: Canvas, x: Float, y: Float, size: Float, showTagline: Boolean) {
            logoBitmap?.let { bitmap ->
                val inset = if (showTagline) 0f else size * 0.04f
                canvas.drawBitmap(
                    bitmap,
                    Rect(0, 0, bitmap.width, bitmap.height),
                    RectF(x + inset, y + inset, x + size - inset, y + size - inset),
                    Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                )
                return
            }

            text(canvas, "BD", x, y, size, size * 0.54f, dark, bold, maxLines = 1)
            fillRound(canvas, x + size * .53f, y + size * .05f, x + size * .92f, y + size * .42f, size * .18f, danger)
            val map = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE }
            val cx = x + size * .72f
            val cy = y + size * .24f
            canvas.drawCircle(cx, cy, size * .065f, map)
            canvas.drawRect(cx - size * .025f, cy, cx + size * .028f, cy + size * .10f, map)
            text(canvas, "TAX", x, y + size * .43f, size, size * .38f, dark, bold, maxLines = 1)
            if (showTagline) {
                line(canvas, x, y + size * .88f, x + size * .88f, y + size * .88f, secondary, 0.6f)
                text(canvas, "SMART TAX. STRONG NATION.", x, y + size * .91f, size, size * .055f, dark, bold, Layout.Alignment.ALIGN_CENTER, 1)
            }
        }

        private fun drawGrowthIllustration(canvas: Canvas, x: Float, y: Float) {
            val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(0f, y + 25f, 0f, y + 90f, Color.rgb(121, 218, 76), secondary, Shader.TileMode.CLAMP)
            }
            listOf(20f, 30f, 42f, 57f).forEachIndexed { i, h ->
                canvas.drawRoundRect(RectF(x + 34f + i * 18f, y + 78f - h, x + 46f + i * 18f, y + 78f), 2f, 2f, barPaint)
            }
            val arrow = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.STROKE; strokeWidth = 4f }
            val path = Path().apply { moveTo(x, y + 70f); cubicTo(x + 48f, y + 64f, x + 85f, y + 36f, x + 116f, y + 4f) }
            canvas.drawPath(path, arrow)
            val head = Path().apply { moveTo(x + 101f, y + 10f); lineTo(x + 118f, y + 3f); lineTo(x + 113f, y + 22f); close() }
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; canvas.drawPath(head, this) }
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(255, 184, 50)
                repeat(4) { i -> canvas.drawOval(RectF(x + 77f, y + 73f - i * 3f, x + 100f, y + 80f - i * 3f), this) }
            }
        }

        private fun drawTaxDocument(canvas: Canvas, x: Float, y: Float) {
            val paper = Path().apply {
                moveTo(x, y + 2f); lineTo(x + 58f, y + 2f); lineTo(x + 72f, y + 16f)
                lineTo(x + 68f, y + 84f); lineTo(x - 7f, y + 80f); close()
            }
            Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(249, 253, 244); canvas.drawPath(paper, this) }
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(219, 232, 211)
                val fold = Path().apply { moveTo(x + 58f, y + 2f); lineTo(x + 72f, y + 16f); lineTo(x + 57f, y + 16f); close() }
                canvas.drawPath(fold, this)
            }
            text(canvas, "TAX", x + 8f, y + 11f, 46f, 15f, primary, bold)
            repeat(3) { i -> fillRound(canvas, x + 8f, y + 37f + i * 10f, x + 48f - i * 4f, y + 40f + i * 10f, 1.5f, Color.rgb(170, 201, 160)) }
            strokeRound(canvas, x + 34f, y + 57f, x + 61f, y + 84f, 14f, secondary, 1.5f)
            line(canvas, x + 41f, y + 70f, x + 48f, y + 76f, secondary, 2f)
            line(canvas, x + 48f, y + 76f, x + 57f, y + 65f, secondary, 2f)
        }

        private fun drawSectionIcon(canvas: Canvas, type: String, x: Float, y: Float, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 1.5f; strokeCap = Paint.Cap.ROUND }
            when (type) {
                "profile", "person" -> { p.style = Paint.Style.FILL; canvas.drawCircle(x + 5f, y + 3.5f, 3f, p); canvas.drawRoundRect(RectF(x, y + 7f, x + 10f, y + 12f), 3f, 3f, p) }
                "bars" -> { p.style = Paint.Style.FILL; canvas.drawRect(x, y + 7f, x + 2f, y + 12f, p); canvas.drawRect(x + 4f, y + 4f, x + 6f, y + 12f, p); canvas.drawRect(x + 8f, y, x + 10f, y + 12f, p) }
                "calendar" -> drawCalendarIcon(canvas, x, y, color)
                "location" -> { canvas.drawCircle(x + 5f, y + 4f, 4f, p); canvas.drawLine(x + 2f, y + 7f, x + 5f, y + 12f, p); canvas.drawLine(x + 8f, y + 7f, x + 5f, y + 12f, p); canvas.drawCircle(x + 5f, y + 4f, 1f, p) }
                "calculator" -> { canvas.drawRoundRect(RectF(x, y, x + 10f, y + 12f), 1f, 1f, p); canvas.drawRect(x + 2f, y + 2f, x + 8f, y + 4f, p); repeat(2) { r -> repeat(2) { c -> canvas.drawCircle(x + 3f + c * 4f, y + 7f + r * 3f, .7f, p) } } }
                "shield" -> drawShieldIcon(canvas, x - 1f, y - 1f, 12f, color)
                "payment" -> { canvas.drawRect(x, y + 1f, x + 10f, y + 11f, p); repeat(3) { i -> canvas.drawLine(x + 2f, y + 4f + i * 2.5f, x + 8f, y + 4f + i * 2.5f, p) } }
                "document" -> drawSmallDocumentIcon(canvas, x, y, color)
                "down" -> { canvas.drawCircle(x + 5f, y + 5f, 5f, p); canvas.drawLine(x + 5f, y + 2f, x + 5f, y + 8f, p); canvas.drawLine(x + 2.5f, y + 5.5f, x + 5f, y + 8f, p); canvas.drawLine(x + 7.5f, y + 5.5f, x + 5f, y + 8f, p) }
                "card" -> { canvas.drawRoundRect(RectF(x, y + 1f, x + 11f, y + 9f), 1f, 1f, p); canvas.drawLine(x, y + 4f, x + 11f, y + 4f, p) }
            }
        }

        private fun drawSmallDocumentIcon(canvas: Canvas, x: Float, y: Float, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 1.2f }
            canvas.drawRect(x, y, x + 10f, y + 12f, p)
            repeat(3) { i -> canvas.drawLine(x + 2f, y + 3f + i * 3f, x + 8f, y + 3f + i * 3f, p) }
        }

        private fun drawCalendarIcon(canvas: Canvas, x: Float, y: Float, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = 1.2f }
            canvas.drawRoundRect(RectF(x, y + 2f, x + 11f, y + 12f), 1.5f, 1.5f, p)
            canvas.drawLine(x, y + 5f, x + 11f, y + 5f, p)
            canvas.drawLine(x + 3f, y, x + 3f, y + 4f, p)
            canvas.drawLine(x + 8f, y, x + 8f, y + 4f, p)
        }

        private fun drawShieldIcon(canvas: Canvas, x: Float, y: Float, size: Float, color: Int) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = size * .1f }
            val path = Path().apply { moveTo(x + size / 2, y); lineTo(x + size, y + size * .18f); lineTo(x + size * .86f, y + size * .72f); lineTo(x + size / 2, y + size); lineTo(x + size * .14f, y + size * .72f); lineTo(x, y + size * .18f); close() }
            canvas.drawPath(path, p)
            canvas.drawLine(x + size * .3f, y + size * .48f, x + size * .45f, y + size * .64f, p)
            canvas.drawLine(x + size * .45f, y + size * .64f, x + size * .72f, y + size * .34f, p)
        }

        private fun drawQr(canvas: Canvas, x: Float, y: Float, size: Float) {
            val bitmap = qrBitmap ?: return
            canvas.drawBitmap(bitmap, Rect(0, 0, bitmap.width, bitmap.height), RectF(x, y, x + size, y + size), Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
        }

        private fun shadowCard(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, radius: Float) {
            fillRound(canvas, left + 1.5f, top + 2f, right + 1.5f, bottom + 2f, radius, Color.argb(28, 47, 100, 51))
            fillRound(canvas, left, top, right, bottom, radius, Color.WHITE)
            strokeRound(canvas, left, top, right, bottom, radius, Color.rgb(213, 230, 207), 0.45f)
        }

        private fun equationSymbol(canvas: Canvas, symbol: String, x: Float, y: Float) {
            text(canvas, symbol, x, y, 13f, 14f, dark, bold, Layout.Alignment.ALIGN_CENTER, 1)
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

        private fun moreInvestmentsLabel(count: Int): String {
            return if (report.isBangla) {
                "+ আরও ${localizedNumberText(count.toString())}টি পৃষ্ঠা ২-এ"
            } else {
                "+ $count more on page 2"
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

        private fun titleSize(size: Float): Float = if (report.isBangla) size * 0.86f else size

        private fun headingSize(size: Float): Float = if (report.isBangla) size * 0.92f else size

        private fun bodySize(size: Float): Float = if (report.isBangla) size * 0.9f else size

        private fun badgeSize(size: Float): Float = if (report.isBangla) size * 0.88f else size

        private fun fillRound(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, radius: Float, color: Int) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; canvas.drawRoundRect(RectF(left, top, right, bottom), radius, radius, this) }
        }

        private fun strokeRound(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, radius: Float, color: Int, width: Float) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; style = Paint.Style.STROKE; strokeWidth = width; canvas.drawRoundRect(RectF(left, top, right, bottom), radius, radius, this) }
        }

        private fun line(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float, color: Int, width: Float) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; strokeWidth = width; canvas.drawLine(x1, y1, x2, y2, this) }
        }

        private fun dotPattern(canvas: Canvas, x: Float, y: Float, rows: Int, columns: Int, gap: Float, color: Int) {
            Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; repeat(rows) { row -> repeat(columns) { col -> canvas.drawCircle(x + col * gap, y + row * gap, 1.2f, this) } } }
        }

        private fun textWidth(value: String, size: Float, typeface: Typeface): Float = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = size; this.typeface = typeface }.measureText(value)

        private fun text(
            canvas: Canvas,
            value: String,
            x: Float,
            y: Float,
            width: Float,
            size: Float,
            color: Int,
            typeface: Typeface,
            alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
            maxLines: Int = 2
        ) {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = size; this.color = color; this.typeface = typeface }
            val layout = StaticLayout.Builder.obtain(value, 0, value.length, paint, width.toInt().coerceAtLeast(1))
                .setAlignment(alignment)
                .setIncludePad(false)
                .setLineSpacing(0f, 1.02f)
                .setMaxLines(maxLines)
                .setEllipsize(TextUtils.TruncateAt.END)
                .build()
            canvas.save()
            canvas.translate(x, y)
            layout.draw(canvas)
            canvas.restore()
        }
    }
}
