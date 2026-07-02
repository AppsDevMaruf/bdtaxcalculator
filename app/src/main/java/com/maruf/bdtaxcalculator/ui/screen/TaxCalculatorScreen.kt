package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.LocalTaxPreferenceStore
import com.maruf.bdtaxcalculator.tax.SalaryBreakdown
import com.maruf.bdtaxcalculator.tax.TaxBreakdown
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.TaxPaymentAdjustment
import com.maruf.bdtaxcalculator.tax.TaxResult
import com.maruf.bdtaxcalculator.tax.TaxSummary
import com.maruf.bdtaxcalculator.tax.TaxpayerType
import com.maruf.bdtaxcalculator.tax.calculateInvestmentRebate
import com.maruf.bdtaxcalculator.tax.calculateSalaryBreakdown
import com.maruf.bdtaxcalculator.tax.calculateTax
import com.maruf.bdtaxcalculator.tax.calculateTaxFreeLimit
import com.maruf.bdtaxcalculator.tax.calculateTaxPaymentAdjustment
import com.maruf.bdtaxcalculator.tax.calculateTaxSummary
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.tax.formatBengaliPercent
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDivider
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientBottom
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientTop
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfo
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoDark
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInk
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPositive
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeNavInactive
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable
import kotlinx.coroutines.delay

private val investmentSaver: Saver<List<InvestmentInputData>, Any> = mapSaver(
    save = { list -> list.associate { it.type to it.amount } },
    restore = { map ->
        TaxDefaults.investmentOptions.map {
            it.copy(amount = (map[it.type] as? String) ?: "")
        }
    }
)

private fun normalizeNumericInput(input: String, maxLength: Int = MaxMoneyInputLength): String {
    return buildString {
        input.forEach { char ->
            when (char) {
                in '0'..'9' -> append(char)
                in '০'..'৯' -> append(('0'.code + char.code - '০'.code).toChar())
                in '٠'..'٩' -> append(('0'.code + char.code - '٠'.code).toChar())
            }
        }
    }.take(maxLength)
}

@Composable
private fun incomeYearLabel(): String = localizedText(TaxDefaults.incomeYearLabel, "2025-26")

@Composable
private fun assessmentYearLabel(): String = localizedText(TaxDefaults.assessmentYearLabel, "2026-27")

@Composable
private fun taxYearLabel(): String = assessmentYearLabel()

@Composable
private fun TaxpayerType.localizedLabel(): String {
    return when (id) {
        "general" -> localizedText("সাধারণ করদাতা", "General taxpayer")
        "women" -> localizedText("মহিলা করদাতা", "Female taxpayer")
        "senior" -> localizedText("সিনিয়র সিটিজেন (৬৫+)", "Senior citizen (65+)")
        "disabled" -> localizedText("তৃতীয় লিঙ্গ / প্রতিবন্ধী", "Third gender / disabled")
        "freedomFighter" -> localizedText(
            "যুদ্ধাহত মুক্তিযোদ্ধা / আহত জুলাই যোদ্ধা",
            "War-wounded freedom fighter / injured July fighter"
        )
        else -> label
    }
}

@Composable
private fun assessmentTypeLabel(assessmentType: String): String {
    return when (assessmentType) {
        LocalTaxPreferenceStore.assessmentNew -> localizedText("নতুন করদাতা", "New taxpayer")
        else -> localizedText("বিদ্যমান করদাতা", "Existing taxpayer")
    }
}

@Composable
private fun assessmentTypeDescription(assessmentType: String, selectedMinimumTax: Double): String {
    return when (assessmentType) {
        LocalTaxPreferenceStore.assessmentNew -> localizedText(
            "ন্যূনতম কর: ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}",
            "Minimum tax: BDT ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}"
        )
        else -> localizedText(
            "সকল এলাকার ন্যূনতম কর: ${formatBengaliNumber(selectedMinimumTax.toLong())}",
            "Minimum tax for all locations: BDT ${formatBengaliNumber(selectedMinimumTax.toLong())}"
        )
    }
}

@Composable
private fun InvestmentInputData.localizedTitle(): String {
    return when (type) {
        "dse" -> localizedText("DSE শেয়ার", "DSE shares")
        "sanchaypatra" -> localizedText("সঞ্চয়পত্র", "Savings certificates")
        "dps" -> localizedText("DPS (ডিপোজিট পেনশন স্কিম)", "DPS (Deposit Pension Scheme)")
        "mutual" -> localizedText("মিউচুয়াল ফান্ড", "Mutual fund")
        "insurance" -> localizedText("লাইফ ইন্স্যুরেন্স", "Life insurance")
        else -> title
    }
}

@Composable
fun TaxCalculatorScreen(
    onBack: (() -> Unit)? = null,
    onRequestInAppReview: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val hideKeyboardOnScrollConnection = rememberKeyboardDismissOnScrollConnection()
    val defaultTaxpayerType = remember(context) {
        LocalTaxPreferenceStore.getDefaultTaxpayerType(context)
    }
    val defaultAssessmentType = remember(context) {
        LocalTaxPreferenceStore.getAssessmentType(context)
    }
    var grossSalary by rememberSaveable { mutableStateOf("") }
    var yearlyBonus by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf(defaultTaxpayerType) }
    var selectedAssessmentType by rememberSaveable { mutableStateOf(defaultAssessmentType) }
    var disabledDependentCount by rememberSaveable { mutableStateOf(0) }
    var adjustableSourceTax by rememberSaveable { mutableStateOf("") }
    var advanceTax by rememberSaveable { mutableStateOf("") }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    var hasLoggedTaxCalculation by rememberSaveable { mutableStateOf(false) }
    var hasLoggedTaxCreditUsage by rememberSaveable { mutableStateOf(false) }
    var investments by rememberSaveable(stateSaver = investmentSaver) {
        mutableStateOf(TaxDefaults.investmentOptions)
    }

    val scrollState = rememberScrollState()
    val taxpayerTypes = remember { TaxDefaults.taxpayerTypes }
    val currentType = taxpayerTypes.first { it.id == selectedType }
    val effectiveTaxFreeLimit = calculateTaxFreeLimit(
        baseTaxFreeLimit = currentType.taxFreeLimit,
        disabledDependentCount = disabledDependentCount
    )
    val minimumTax = if (selectedAssessmentType == LocalTaxPreferenceStore.assessmentNew) {
        TaxDefaults.newAssessmentMinimumTax
    } else {
        TaxDefaults.minimumTax
    }

    val salaryBreakdown = calculateSalaryBreakdown(
        grossSalary = grossSalary.toLongOrNull() ?: 0L,
        yearlyBonus = yearlyBonus.toLongOrNull() ?: 0L
    )
    val investmentRebate = calculateInvestmentRebate(investments, salaryBreakdown.taxableIncome)
    val result = calculateTax(
        income = salaryBreakdown.taxableIncome,
        taxFreeLimit = effectiveTaxFreeLimit,
        investmentRebate = investmentRebate,
        minimumTax = minimumTax
    )
    val paymentAdjustment = calculateTaxPaymentAdjustment(
        taxLiability = result.taxAfterRebate,
        adjustableSourceTax = adjustableSourceTax.toLongOrNull() ?: 0L,
        advanceTax = advanceTax.toLongOrNull() ?: 0L
    )
    val summary = calculateTaxSummary(
        totalIncome = salaryBreakdown.totalIncome,
        taxAfterRebate = result.taxAfterRebate
    )

    LaunchedEffect(salaryBreakdown.totalIncome, result.taxAfterRebate) {
        if (salaryBreakdown.totalIncome > 0L) {
            delay(1_800)
            if (!hasLoggedTaxCalculation) {
                FirebaseTracker.logEvent("tax_calculation_completed")
                hasLoggedTaxCalculation = true
            }
            onRequestInAppReview("tax_calculation_completed")
        }
    }

    LaunchedEffect(adjustableSourceTax.isNotBlank() || advanceTax.isNotBlank()) {
        if ((adjustableSourceTax.isNotBlank() || advanceTax.isNotBlank()) && !hasLoggedTaxCreditUsage) {
            FirebaseTracker.logEvent("tax_credit_adjustment_used")
            hasLoggedTaxCreditUsage = true
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onBack = onBack,
                onReset = {
                    grossSalary = ""
                    yearlyBonus = ""
                    selectedType = defaultTaxpayerType
                    selectedAssessmentType = defaultAssessmentType
                    disabledDependentCount = 0
                    adjustableSourceTax = ""
                    advanceTax = ""
                    investments = TaxDefaults.investmentOptions
                },
                onInfoClick = { showInfoDialog = true }
            )
        },
        containerColor = CalculatorBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(CalculatorGradientTop, CalculatorGradientMiddle, CalculatorGradientBottom)
                    )
                )
                .imePadding()
        ) {
            if (showInfoDialog) {
                TaxInfoDialog(onDismiss = { showInfoDialog = false })
            }

            // Fixed Header
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                CalculatorOverviewCard(
                    currentType = currentType,
                    salaryBreakdown = salaryBreakdown,
                    summary = summary,
                    taxResult = result,
                    paymentAdjustment = paymentAdjustment,
                    totalInvestment = investments.sumOf { it.amount.toLongOrNull() ?: 0L }
                )
            }

            // Scrollable Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(hideKeyboardOnScrollConnection)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CalculatorInputHub(
                    taxpayerTypes = taxpayerTypes,
                    selectedType = selectedType,
                    onTypeSelect = {
                        selectedType = it
                        LocalTaxPreferenceStore.setDefaultTaxpayerType(context, it)
                    },
                    disabledDependentCount = disabledDependentCount,
                    onDisabledDependentCountChange = {
                        disabledDependentCount = it.coerceIn(0, MaxDisabledDependentCount)
                    },
                    assessmentType = selectedAssessmentType,
                    onAssessmentTypeChange = {
                        selectedAssessmentType = it
                        LocalTaxPreferenceStore.setAssessmentType(context, it)
                    },
                    selectedMinimumTax = minimumTax,
                    grossSalary = grossSalary,
                    yearlyBonus = yearlyBonus,
                    onGrossSalaryChange = { grossSalary = it },
                    onYearlyBonusChange = { yearlyBonus = it },
                )

                if (salaryBreakdown.taxableIncome > effectiveTaxFreeLimit) {
                    TaxPaymentAdjustmentCard(
                        adjustment = paymentAdjustment,
                        adjustableSourceTax = adjustableSourceTax,
                        advanceTax = advanceTax,
                        onAdjustableSourceTaxChange = { adjustableSourceTax = it },
                        onAdvanceTaxChange = { advanceTax = it }
                    )
                }

                SalaryBreakdownCard(salaryBreakdown)

                InvestmentInputSection(
                    investments = investments,
                    onInvestmentChange = { type, value ->
                        investments = investments.map {
                            if (it.type == type) it.copy(amount = value) else it
                        }
                    }
                )

                if (salaryBreakdown.taxableIncome > effectiveTaxFreeLimit) {
                    InvestmentSummaryCard(
                        taxableIncome = salaryBreakdown.taxableIncome,
                        investments = investments,
                        earnedRebate = investmentRebate
                    )
                }

                if (salaryBreakdown.taxableIncome <= effectiveTaxFreeLimit) {
                    TaxFreeCard(
                        limit = effectiveTaxFreeLimit
                    )
                } else {
                    TaxBreakdownCard(result = result)
                }
            }
        }
    }
}

@Composable
private fun AppTopBar(
    onBack: (() -> Unit)?,
    onReset: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (onBack != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = localizedText("ফিরুন", "Back"),
                        tint = CalculatorSuccess,
                        modifier = Modifier
                            .clickable(onClick = onBack)
                            .padding(9.dp)
                            .size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = localizedText("আয়কর ক্যালকুলেটর", "Income Tax Calculator"),
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CalculatorInk,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = localizedText("করবর্ষ ${TaxDefaults.taxYearLabel}", "Tax year ${taxYearLabel()}"),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderActionButton(
                icon = Icons.Default.Refresh,
                label = localizedText("রিসেট", "Reset"),
                onClick = onReset
            )
            HeaderActionButton(
                icon = Icons.Default.Info,
                label = localizedText("তথ্য", "Info"),
                onClick = onInfoClick
            )
        }
    }
}
@Composable
private fun HeaderActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = CalculatorSuccess, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun AnimatedChevron(
    expanded: Boolean,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        animationSpec = tween(durationMillis = 220),
        label = "chevronRotation"
    )

    Icon(
        Icons.Default.ChevronRight,
        contentDescription = if (expanded) localizedText("বন্ধ করুন", "Collapse") else localizedText("খুলুন", "Expand"),
        tint = tint,
        modifier = modifier
            .size(20.dp)
            .graphicsLayer(rotationZ = rotation)
    )
}

@Composable
private fun ExpandableContent(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(140)) + expandVertically(
            animationSpec = tween(240),
            expandFrom = Alignment.Top
        ),
        exit = fadeOut(animationSpec = tween(90)) + shrinkVertically(
            animationSpec = tween(200),
            shrinkTowards = Alignment.Top
        )
    ) {
        content()
    }
}
@Composable
private fun CalculatorOverviewCard(
    currentType: TaxpayerType,
    salaryBreakdown: SalaryBreakdown,
    summary: TaxSummary,
    taxResult: TaxResult,
    paymentAdjustment: TaxPaymentAdjustment,
    totalInvestment: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(localizedText("সারসংক্ষেপ", "Overview"), fontSize = 11.sp, lineHeight = 13.sp, color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
                    Text(
                        currentType.localizedLabel(),
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = TiroBanglaFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = CalculatorAccentSoft,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp).size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("করযোগ্য আয়", "Taxable Income"),
                        value = formatBengaliNumber(salaryBreakdown.taxableIncome),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("অবশিষ্ট কর", "Remaining Tax"),
                        value = formatBengaliNumber(paymentAdjustment.remainingPayable.toLong()),
                        accentColor = CalculatorDanger
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("বিনিয়োগ", "Investment"),
                        value = formatBengaliNumber(totalInvestment),
                        accentColor = CalculatorSuccess
                    )
                }

            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("রিবেট", "Rebate"),
                        value = formatBengaliNumber(taxResult.investmentRebate.toLong()),
                        accentColor = CalculatorInfoDark
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("কার্যকর কর হার", "Tax Rate"),
                        value = formatBengaliPercent(summary.effectiveTaxRatePercent),
                        accentColor = CalculatorPositive
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("মাসিক কর", "Monthly Tax"),
                        value = formatBengaliNumber(summary.monthlyTaxEstimate),
                        accentColor = CalculatorDanger
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(100.dp),
                        label = localizedText("মাসিক প্রকৃত আয়", "Net Monthly Income"),
                        value = formatBengaliNumber(summary.yearlyNetIncomeAfterTax / 12),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                }

            }
        }
    }
}

@Composable
private fun HeroMetricTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    accentColor: Color
) {
    Surface(
        modifier = modifier,
        color = CalculatorAccentSoft,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = CalculatorMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                fontFamily = TiroBanglaFontFamily)
            Text(
                value,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                fontFamily = TiroBanglaFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CalculatorInputHub(
    taxpayerTypes: List<TaxpayerType>,
    selectedType: String,
    onTypeSelect: (String) -> Unit,
    disabledDependentCount: Int,
    onDisabledDependentCountChange: (Int) -> Unit,
    assessmentType: String,
    onAssessmentTypeChange: (String) -> Unit,
    selectedMinimumTax: Double,
    grossSalary: String,
    yearlyBonus: String,
    onGrossSalaryChange: (String) -> Unit,
    onYearlyBonusChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Taxpayer type card — standalone, full bleed LazyRow
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionLabel(
                        localizedText("করদাতার শ্রেণী", "Taxpayer Category"),
                        localizedText("আপনার করদাতার শ্রেণী নির্বাচন করুন", "Choose your taxpayer category")
                    )
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(taxpayerTypes, key = { it.id }) { type ->
                        TaxpayerTypeItem(
                            type = type,
                            isSelected = type.id == selectedType,
                            onSelect = { onTypeSelect(type.id) },
                        )
                    }
                }
                DisabledDependentAllowanceRow(
                    count = disabledDependentCount,
                    onCountChange = onDisabledDependentCountChange
                )
            }
        }

        TaxRuleSelectionCard(
            assessmentType = assessmentType,
            onAssessmentTypeChange = onAssessmentTypeChange,
            selectedMinimumTax = selectedMinimumTax
        )

        // Income input card — separate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GrossSalaryInput(grossSalary, yearlyBonus, onGrossSalaryChange, onYearlyBonusChange)
            }
        }
    }
}

@Composable
private fun DisabledDependentAllowanceRow(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        color = CalculatorAccentSoft,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    localizedText("প্রতিবন্ধী সন্তান/পোষ্য", "Disabled child/dependent"),
                    fontSize = 12.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalculatorFieldText,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        "প্রতি জনে করমুক্ত সীমা +${formatBengaliNumber(TaxDefaults.disabledDependentAllowance)}",
                        "+BDT ${formatBengaliNumber(TaxDefaults.disabledDependentAllowance)} tax-free limit each"
                    ),
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
            DependentCountButton(
                icon = Icons.Default.Remove,
                enabled = count > 0,
                contentDescription = localizedText("কমান", "Decrease"),
                onClick = { onCountChange(count - 1) }
            )
            Text(
                formatBengaliNumber(count.toLong()),
                modifier = Modifier.width(22.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = CalculatorInk,
                fontFamily = TiroBanglaFontFamily
            )
            DependentCountButton(
                icon = Icons.Default.Add,
                enabled = count < MaxDisabledDependentCount,
                contentDescription = localizedText("বাড়ান", "Increase"),
                onClick = { onCountChange(count + 1) }
            )
        }
    }
}

@Composable
private fun DependentCountButton(
    icon: ImageVector,
    enabled: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(30.dp)
            .noRippleClickable {
                if (enabled) onClick()
            },
        color = if (enabled) CalculatorSuccess.copy(alpha = 0.12f) else CalculatorSurfaceAlt,
        shape = CircleShape
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.padding(7.dp),
            tint = if (enabled) CalculatorSuccess else CalculatorMutedSoft
        )
    }
}
@Composable
private fun TaxRuleSelectionCard(
    assessmentType: String,
    onAssessmentTypeChange: (String) -> Unit,
    selectedMinimumTax: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionLabel(
                localizedText("ন্যূনতম করের ধরন", "Minimum Tax Status"),
                localizedText(
                    "বিদ্যমান করদাতার জন্য ৫,০০০; নতুন করদাতার জন্য ১,০০০ টাকা",
                    "BDT 5,000 for existing taxpayers; BDT 1,000 for new taxpayers"
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SelectableRuleRow(
                    title = assessmentTypeLabel(LocalTaxPreferenceStore.assessmentRegular),
                    subtitle = localizedText(
                        "সকল এলাকার জন্য ন্যূনতম কর ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())}",
                        "Minimum tax BDT ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())} for all locations"
                    ),
                    selected = assessmentType == LocalTaxPreferenceStore.assessmentRegular,
                    onClick = { onAssessmentTypeChange(LocalTaxPreferenceStore.assessmentRegular) }
                )
                SelectableRuleRow(
                    title = assessmentTypeLabel(LocalTaxPreferenceStore.assessmentNew),
                    subtitle = localizedText(
                        "নতুন করদাতার ন্যূনতম কর ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}",
                        "Minimum tax for a new taxpayer is BDT ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}"
                    ),
                    selected = assessmentType == LocalTaxPreferenceStore.assessmentNew,
                    onClick = { onAssessmentTypeChange(LocalTaxPreferenceStore.assessmentNew) }
                )
            }

            Surface(
                color = CalculatorAccentSoft,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    assessmentTypeDescription(assessmentType, selectedMinimumTax),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = CalculatorSuccess,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun SelectableRuleRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        color = if (selected) CalculatorSuccess.copy(alpha = 0.08f) else CalculatorSurfaceAlt,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) CalculatorSuccess else CalculatorBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (selected) CalculatorSuccess else CalculatorMutedSoft
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    title,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (selected) CalculatorInk else CalculatorFieldText,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    subtitle,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = if (selected) CalculatorSuccess else CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontSize = 15.sp, lineHeight = 10.sp, fontWeight = FontWeight.Bold, color = CalculatorInk, fontFamily = TiroBanglaFontFamily)
        Text(subtitle, fontSize = 11.sp,lineHeight = 16.sp, color = CalculatorMutedSoft, fontFamily = TiroBanglaFontFamily)
    }
}

@Composable
private fun TaxInfoDialog(onDismiss: () -> Unit) {
   Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .heightIn(max = 620.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    localizedText("এই অ্যাপে যেভাবে হিসাব হয়", "How this app calculates tax"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = TiroBanglaFontFamily
                )
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BulletInfoRow(localizedText("মাসিক মোট বেতন × ১২ + বার্ষিক বোনাস = মোট বার্ষিক আয়।", "Monthly gross salary × 12 + yearly bonus = total annual income."))
                    BulletInfoRow(localizedText("বেতন আয়ের ছাড় হিসেবে মোট আয়ের ১/৩ অংশ অথবা ${formatBengaliNumber(TaxDefaults.maxTotalExemption)} টাকা, যেটি কম, বাদ দেওয়া হয়।", "As salary exemption, the lower of 1/3 of total income or BDT ${formatBengaliNumber(TaxDefaults.maxTotalExemption)} is deducted."))
                    BulletInfoRow(localizedText("মোট বার্ষিক আয় - বেতন ছাড় = নিট করযোগ্য আয়।", "Total annual income - salary exemption = net taxable income."))
                    BulletInfoRow(localizedText("নির্বাচিত করদাতা শ্রেণির করমুক্ত সীমা পর্যন্ত কর শূন্য ধরা হয়।", "Tax is zero up to the selected taxpayer category's tax-free limit."))
                    BulletInfoRow(localizedText("প্রতিবন্ধী সন্তান/পোষ্য প্রতি করমুক্ত সীমায় আরও ${formatBengaliNumber(TaxDefaults.disabledDependentAllowance)} টাকা যোগ হয়। বাবা-মা উভয়েই করদাতা হলে যেকোনো একজন এই সুবিধা নিতে পারবেন।", "An additional BDT ${formatBengaliNumber(TaxDefaults.disabledDependentAllowance)} tax-free allowance applies for each disabled child/dependent. If both parents are taxpayers, only one may claim it."))
                    BulletInfoRow(localizedText("আয়বর্ষ ${TaxDefaults.incomeYearLabel} এবং অ্যাসেসমেন্ট ইয়ার ${TaxDefaults.assessmentYearLabel} অনুযায়ী কর ধাপ: পরবর্তী ৩,০০,০০০ টাকায় ১০%, পরবর্তী ৪,০০,০০০ টাকায় ১৫%, পরবর্তী ৫,০০,০০০ টাকায় ২০%, পরবর্তী ২০,০০,০০০ টাকায় ২৫%, এরপর ৩০%।", "For income year ${incomeYearLabel()} and assessment year ${assessmentYearLabel()}, slab rates are: next BDT 300,000 at 10%, next BDT 400,000 at 15%, next BDT 500,000 at 20%, next BDT 2,000,000 at 25%, then 30%."))
                    BulletInfoRow(localizedText("বিনিয়োগ রিবেট হিসেবে করযোগ্য আয়ের ৩%, প্রকৃত যোগ্য বিনিয়োগের ১০%, অথবা ৭.৫ লাখ টাকার মধ্যে কমটি ধরা হয়।", "Investment rebate is the lower of 3% of taxable income, 10% of actual eligible investment, or BDT 750,000."))
                    BulletInfoRow(localizedText("চূড়ান্ত প্রদেয় কর = ধাপ অনুযায়ী মোট কর - বিনিয়োগ রিবেট। করমুক্ত সীমা ছাড়ালে সকল এলাকার জন্য ন্যূনতম কর ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())} টাকা; নতুন করদাতার ক্ষেত্রে ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())} টাকা।", "Final payable tax = slab tax - investment rebate. After crossing the tax-free limit, minimum tax is BDT ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())} for all locations, or BDT ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())} for a new taxpayer."))
                    BulletInfoRow(localizedText("অবশিষ্ট প্রদেয় কর = রিবেটের পর করদায় - সমন্বয়যোগ্য উৎসে কর - পরিশোধিত অগ্রিম কর। অতিরিক্ত credit আলাদাভাবে দেখানো হয়।", "Remaining payable tax = tax liability after rebate - adjustable source tax - advance tax paid. Any excess credit is shown separately."))
                    BulletInfoRow(localizedText("সব হিসাব ডিভাইসেই হয়; আপনার ইনপুট কোনো সার্ভার বা ডেটাবেসে পাঠানো বা সংরক্ষণ করা হয় না।", "All calculations happen on device; your inputs are not sent to or saved on any server or database."))
                }
            }
        }
    }
}

@Composable
private fun BulletInfoRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            "•",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CalculatorSuccess,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            text,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = CalculatorFieldText,
            lineHeight = 20.sp,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun GrossSalaryInput(
    grossSalary: String,
    yearlyBonus: String,
    onGrossSalaryChange: (String) -> Unit,
    onYearlyBonusChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel(
            localizedText("আয়ের তথ্য", "Income Information"),
            localizedText("হিসাবের জন্য আপনার আয়ের তথ্য দিন", "Enter your income details for calculation")
        )

        CurrencyInputField(
            value = grossSalary,
            onValueChange = onGrossSalaryChange,
            label = localizedText("মাসিক মোট বেতন", "Monthly Gross Salary"),
            placeholder = "0"
        )

        CurrencyInputField(
            value = yearlyBonus,
            onValueChange = onYearlyBonusChange,
            label = localizedText("বছরে মোট বোনাস (উৎসব ও অন্যান্য)", "Yearly Bonus (Festival and Others)"),
            placeholder = "0"
        )
    }
}

@Composable
fun CurrencyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, fontSize = 12.sp, lineHeight = 2.sp, fontWeight = FontWeight.Medium, color = CalculatorFieldText, fontFamily = TiroBanglaFontFamily)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CalculatorSurfaceAlt,
            border = BorderStroke(1.dp, CalculatorBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (leading != null) {
                    leading()
                } else {
                    Text(localizedText("৳", "BDT"), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = TiroBanglaFontFamily)
                }
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(normalizeNumericInput(it)) },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = TiroBanglaFontFamily
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) Text(placeholder, color = HomeNavInactive, fontSize = 16.sp, fontFamily = TiroBanglaFontFamily)
                        innerTextField()
                    }
                )
            }
        }
    }
}

@Composable
private fun SalaryBreakdownCard(salary: SalaryBreakdown) {
    var isMonthlyExpanded by rememberSaveable { mutableStateOf(false) }
    var isYearlyExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionLabel(
                localizedText("বেতন বিভাজন", "Salary Breakdown"),
                localizedText("আপনার মাসিক ও বাৎসরিক আয়ের বিস্তারিত", "Monthly and yearly income details")
            )

            DetailPanel(
                title = localizedText("মাসিক বিভাজন", "Monthly Breakdown"),
                color = MaterialTheme.colorScheme.primary,
                expanded = isMonthlyExpanded,
                onToggle = { isMonthlyExpanded = !isMonthlyExpanded },
                summaryContent = {
                    SalaryItem(localizedText("মোট মাসিক", "Total Monthly"), salary.grossSalary, isTotal = true)
                }
            ) {
                SalaryItem(localizedText("মূল বেতন", "Basic Salary"), salary.basicSalary)
                SalaryItem(localizedText("বাড়ি ভাড়া (মূল বেতনের ৫০%)", "House Rent (50% of Basic)"), salary.houseRent)
                SalaryItem(localizedText("চিকিৎসা (মূল বেতনের ১০%)", "Medical (10% of Basic)"), salary.medical)
                SalaryItem(localizedText("যাতায়াত (স্থির ৫%)", "Conveyance (Fixed 5%)"), salary.conveyance)
            }

            DetailPanel(
                title = localizedText("বাৎসরিক বিশ্লেষণ", "Yearly Analysis"),
                color = CalculatorInfo,
                expanded = isYearlyExpanded,
                onToggle = { isYearlyExpanded = !isYearlyExpanded },
                summaryContent = {
                    YearlyItem(localizedText("নিট করযোগ্য আয়", "Net Taxable Income"), salary.taxableIncome, isTotal = true)
                }
            ) {
                YearlyItem(localizedText("মোট বাৎসরিক বেতন", "Total Annual Income"), salary.totalIncome)
                YearlyItem(localizedText("মোট বোনাস", "Total Bonus"), salary.yearlyBonus)
                YearlyItem(localizedText("স্ট্যান্ডার্ড ছাড়", "Standard Exemption"), salary.totalExemption, isExempt = true)
            }
        }
    }
}

@Composable
private fun DetailPanel(
    title: String,
    color: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
    summaryContent: @Composable ColumnScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(5.dp, 14.dp).background(color, RoundedCornerShape(2.dp)))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = TiroBanglaFontFamily)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                if (expanded) localizedText("বন্ধ করুন", "Collapse") else localizedText("বিস্তারিত", "Details"),
                fontSize = 11.sp,
                color = CalculatorMuted,
                fontWeight = FontWeight.Medium,
                fontFamily = TiroBanglaFontFamily
            )
            AnimatedChevron(
                expanded = expanded,
                tint = color
            )
        }
        Surface(
            color = CalculatorPanel,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CalculatorDivider)
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize(animationSpec = tween(durationMillis = 240))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ExpandableContent(expanded = expanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        content()
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = CalculatorDivider)
                    }
                }
                summaryContent()
            }
        }
    }
}

@Composable
private fun SalaryItem(label: String, amount: Long, isTotal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = if (isTotal) CalculatorInk else CalculatorMuted, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal, fontFamily = TiroBanglaFontFamily)
        Text(formatBengaliNumber(amount), fontSize = 12.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium, color = CalculatorInk, fontFamily = TiroBanglaFontFamily)
    }
}

@Composable
private fun YearlyItem(
    label: String,
    amount: Long,
    isTotal: Boolean = false,
    isExempt: Boolean = false,
    isInvestment: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = if (isTotal) CalculatorInk else CalculatorMuted, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal, fontFamily = TiroBanglaFontFamily)
        val prefix = if (isExempt) "- " else if (isInvestment) "+ " else ""
        val color = if (isExempt) CalculatorDanger else if (isTotal) MaterialTheme.colorScheme.onBackground else CalculatorInk
        Text("$prefix${formatBengaliNumber(amount)}", fontSize = 12.sp, fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium, color = color, fontFamily = TiroBanglaFontFamily)
    }
}

@Composable
private fun InvestmentInputSection(
    investments: List<InvestmentInputData>,
    onInvestmentChange: (String, String) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(durationMillis = 240))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel(
                    localizedText("বিনিয়োগ রেয়াত", "Investment Rebate"),
                    localizedText("আপনার বিনিয়োগ তথ্য দিন", "Enter your investment details")
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        if (isExpanded) localizedText("বন্ধ করুন", "Collapse") else localizedText("বিনিয়োগ যোগ করুন", "Add Investment"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = TiroBanglaFontFamily
                    )
                    AnimatedChevron(
                        expanded = isExpanded,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            ExpandableContent(expanded = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    investments.forEach { investment ->
                        CurrencyInputField(
                            value = investment.amount,
                            onValueChange = { onInvestmentChange(investment.type, it) },
                            label = investment.localizedTitle(),
                            placeholder = "0"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InvestmentSummaryCard(
    taxableIncome: Long,
    investments: List<InvestmentInputData>,
    earnedRebate: Double
) {
    val totalInvestment = investments.sumOf { it.amount.toLongOrNull() ?: 0L }
    val maxRebateLimit = minOf(
        taxableIncome * TaxDefaults.incomeBasedInvestmentRebateRate,
        TaxDefaults.maxInvestmentRebate
    )
    
    // Max investment required to get max rebate based on the current rebate rate.
    val maxInvestmentRequired = (maxRebateLimit / TaxDefaults.investmentRebateRate).toLong()
    val progress = if (maxRebateLimit > 0) (earnedRebate / maxRebateLimit).toFloat().coerceIn(0f, 1f) else 0f
    val remainingInvestmentNeeded = maxOf(0L, maxInvestmentRequired - totalInvestment)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    localizedText("বিনিয়োগ ও রিবেট", "Investment and Rebate"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CalculatorInk,
                    fontFamily = TiroBanglaFontFamily
                )
                Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    tint = CalculatorSuccess,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Progress Section
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        localizedText("রিবেট সীমা ব্যবহার", "Rebate Limit Used"),
                        fontSize = 11.sp,
                        lineHeight = 10.sp,
                        color = CalculatorMuted,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Text(
                        "${formatBengaliNumber((progress * 100).toInt().toLong())}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalculatorInk,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
                LinearProgressIndicator(
                                progress = { progress },
                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(
                                            color = CalculatorSurfaceAlt,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .clip(RoundedCornerShape(50)),
                color = CalculatorSuccess,
                trackColor = Color.Transparent,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InvestmentStatBox(
                    modifier = Modifier.weight(1f),
                    label = localizedText("মোট বিনিয়োগ", "Total Investment"),
                    value = formatBengaliNumber(totalInvestment)
                )
                InvestmentStatBox(
                    modifier = Modifier.weight(1f),
                    label = localizedText("অর্জিত রিবেট", "Earned Rebate"),
                    value = formatBengaliNumber(earnedRebate.toLong())
                )
            }

            // Tip Section
            if (remainingInvestmentNeeded > 0) {
                Text(
                    localizedText(
                        "টিপস: আরও ${formatBengaliNumber(remainingInvestmentNeeded)} টাকা বিনিয়োগ করলে রিবেট সর্বোচ্চ সীমায় পৌঁছাবে।",
                        "Tip: Invest BDT ${formatBengaliNumber(remainingInvestmentNeeded)} more to reach the maximum rebate limit."
                    ),
                    fontSize = 12.sp,
                    color = CalculatorInfo,
                    lineHeight = 16.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            } else {
                Text(
                    localizedText(
                        "অভিনন্দন! আপনি আপনার আয়ের বিপরীতে সর্বোচ্চ রিবেট সীমা অর্জন করেছেন।",
                        "Great! You have reached the maximum rebate limit for your income."
                    ),
                    fontSize = 12.sp,
                    color = CalculatorSuccess,
                    lineHeight = 20.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun InvestmentStatBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color = CalculatorSurfaceAlt.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                label,
                fontSize = 13.sp,
                color = CalculatorMuted,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                localizedText("৳ $value", "BDT $value"),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CalculatorInk,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun TaxPaymentAdjustmentCard(
    adjustment: TaxPaymentAdjustment,
    adjustableSourceTax: String,
    advanceTax: String,
    onAdjustableSourceTaxChange: (String) -> Unit,
    onAdvanceTaxChange: (String) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val hasExcessPayment = adjustment.excessPaid > 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CalculatorDivider)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(durationMillis = 240))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SectionLabel(
                        localizedText("পরিশোধিত কর সমন্বয়", "Paid Tax Adjustment"),
                        localizedText(
                            "সমন্বয়যোগ্য উৎসে কর ও অগ্রিম কর",
                            "Adjustable source tax and advance tax"
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        if (isExpanded) localizedText("বন্ধ করুন", "Collapse")
                        else localizedText("কর যোগ করুন", "Add Tax Credit"),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = TiroBanglaFontFamily
                    )
                    AnimatedChevron(
                        expanded = isExpanded,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TaxPaymentMetric(
                    modifier = Modifier.weight(1f),
                    label = localizedText("মোট কর ক্রেডিট", "Total Tax Credit"),
                    amount = adjustment.totalTaxCredit,
                    color = CalculatorInfoDark
                )
                TaxPaymentMetric(
                    modifier = Modifier.weight(1f),
                    label = if (hasExcessPayment) {
                        localizedText("অতিরিক্ত পরিশোধ", "Excess Paid")
                    } else {
                        localizedText("অবশিষ্ট প্রদেয়", "Remaining Payable")
                    },
                    amount = if (hasExcessPayment) adjustment.excessPaid else adjustment.remainingPayable,
                    color = if (hasExcessPayment) CalculatorSuccess else CalculatorDanger
                )
            }

            ExpandableContent(expanded = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CurrencyInputField(
                        value = adjustableSourceTax,
                        onValueChange = onAdjustableSourceTaxChange,
                        label = localizedText(
                            "সমন্বয়যোগ্য উৎসে কর্তিত/সংগৃহীত কর",
                            "Adjustable Tax Deducted/Collected at Source"
                        ),
                        placeholder = "0"
                    )
                    CurrencyInputField(
                        value = advanceTax,
                        onValueChange = onAdvanceTaxChange,
                        label = localizedText("পরিশোধিত অগ্রিম আয়কর", "Advance Income Tax Paid"),
                        placeholder = "0"
                    )

                    Surface(
                        color = CalculatorAccentSoft,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            localizedText(
                                "শুধু চালান, withholding certificate বা যাচাইকৃত রেকর্ড অনুযায়ী সমন্বয়যোগ্য অর্থ দিন। কিছু উৎসে কর minimum/final tax হতে পারে।",
                                "Enter only adjustable amounts supported by a challan, withholding certificate, or verified record. Some source taxes may be minimum/final tax."
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = CalculatorMuted,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }

                    HorizontalDivider(color = CalculatorDivider)
                    TaxPaymentAdjustmentRow(
                        label = localizedText("রিবেটের পর করদায়", "Tax liability after rebate"),
                        amount = adjustment.taxLiability
                    )
                    TaxPaymentAdjustmentRow(
                        label = localizedText("বাদ: মোট কর ক্রেডিট", "Less: total tax credit"),
                        amount = adjustment.totalTaxCredit,
                        isDeduction = true
                    )
                    TaxPaymentAdjustmentRow(
                        label = if (hasExcessPayment) {
                            localizedText("অতিরিক্ত পরিশোধ", "Excess paid")
                        } else {
                            localizedText("অবশিষ্ট প্রদেয় কর", "Remaining payable tax")
                        },
                        amount = if (hasExcessPayment) adjustment.excessPaid else adjustment.remainingPayable,
                        isTotal = true,
                        color = if (hasExcessPayment) CalculatorSuccess else CalculatorDanger
                    )
                }
            }
        }
    }
}

@Composable
private fun TaxPaymentMetric(
    modifier: Modifier,
    label: String,
    amount: Double,
    color: Color
) {
    Surface(
        modifier = modifier,
        color = CalculatorSurfaceAlt,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                label,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = CalculatorMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                localizedText(
                    "৳ ${formatBengaliNumber(amount.toLong())}",
                    "BDT ${formatBengaliNumber(amount.toLong())}"
                ),
                fontSize = 14.sp,
                lineHeight = 17.sp,
                color = color,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun TaxPaymentAdjustmentRow(
    label: String,
    amount: Double,
    isDeduction: Boolean = false,
    isTotal: Boolean = false,
    color: Color = CalculatorInk
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(1f),
            fontSize = 12.sp,
            color = if (isTotal) color else CalculatorMuted,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            "${if (isDeduction && amount > 0.0) "- " else ""}${formatBengaliNumber(amount.toLong())}",
            fontSize = 12.sp,
            color = if (isTotal) color else CalculatorInk,
            fontWeight = if (isTotal) FontWeight.ExtraBold else FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun TaxBreakdownCard(result: TaxResult) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CalculatorDivider)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(durationMillis = 240))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel(
                    localizedText("কর ধাপ অনুযায়ী হিসাব", "Tax Slab Calculation"),
                    localizedText("আপনার আয়ের ওপর ট্যাক্স যেভাবে বসানো হয়েছে", "How tax is applied to your income")
                )
                AnimatedChevron(
                    expanded = isExpanded,
                    tint = CalculatorMuted,
                )
            }

            ExpandableContent(expanded = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    result.breakdown.forEach { slab ->
                        TaxBreakdownRow(slab)
                    }
                }
            }
        }
    }
}

@Composable
private fun TaxBreakdownRow(slab: TaxBreakdown) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CalculatorSurfaceAlt,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(slab.label, fontSize = 11.sp, color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
                Text(localizedText("${slab.rate}% হার", "${slab.rate}% rate"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = TiroBanglaFontFamily)
            }
            Text(formatBengaliNumber(slab.tax.toLong()), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CalculatorInk, fontFamily = TiroBanglaFontFamily)
        }
    }
}

@Composable
private fun TaxpayerTypeItem(
    type: TaxpayerType,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(112.dp)
            .height(88.dp)
            .noRippleClickable(onClick = onSelect),
        color = CalculatorSurfaceAlt,
        shape = RoundedCornerShape(10.dp),
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (isSelected) CalculatorSuccess.copy(alpha = 0.08f) else CalculatorSurfaceAlt,
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) CalculatorSuccess else CalculatorBorder
                ),
                shape = RoundedCornerShape(10.dp),
            ) {}

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = CircleShape,
                    color = if (isSelected) CalculatorSuccess.copy(alpha = 0.15f) else CalculatorDivider
                ) {
                    if (type.icon != null) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(6.dp)
                                .fillMaxSize(),
                            tint = if (isSelected) CalculatorSuccess else CalculatorMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    type.localizedLabel(),
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    color = if (isSelected) CalculatorInk else CalculatorMuted,
                    textAlign = TextAlign.Center,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    localizedText(
                        "করমুক্ত: ${formatBengaliNumber(type.taxFreeLimit / 1000)}k",
                        "Tax-free: ${formatBengaliNumber(type.taxFreeLimit / 1000)}k"
                    ),
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = if (isSelected) CalculatorSuccess else CalculatorMutedSoft,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun TaxFreeCard(limit: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorInfoBackground),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Rule, contentDescription = null, tint = CalculatorInfo, modifier = Modifier.size(26.dp))
            Column {
                Text(localizedText("কর প্রযোজ্য নয়", "No Tax Applies"), fontWeight = FontWeight.Bold, color = CalculatorInfoDark, fontFamily = TiroBanglaFontFamily)
                Text(
                    localizedText(
                        "আপনার আয় ${formatBengaliNumber(limit)} টাকার করমুক্ত সীমার মধ্যে আছে।",
                        "Your income is within the BDT ${formatBengaliNumber(limit)} tax-free limit."
                    ),
                    fontSize = 12.sp,
                    color = CalculatorInfo,
                    lineHeight = 17.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}
