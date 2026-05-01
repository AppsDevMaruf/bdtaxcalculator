package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.tax.InvestmentInputData
import com.maruf.bdtaxcalculator.tax.SalaryBreakdown
import com.maruf.bdtaxcalculator.tax.TaxBreakdown
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.TaxResult
import com.maruf.bdtaxcalculator.tax.TaxSummary
import com.maruf.bdtaxcalculator.tax.TaxpayerType
import com.maruf.bdtaxcalculator.tax.calculateInvestmentRebate
import com.maruf.bdtaxcalculator.tax.calculateSalaryBreakdown
import com.maruf.bdtaxcalculator.tax.calculateTax
import com.maruf.bdtaxcalculator.tax.calculateTaxSummary
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDangerSoft
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
fun TaxCalculatorScreen(
    onBack: (() -> Unit)? = null
) {
    val hideKeyboardOnScrollConnection = rememberKeyboardDismissOnScrollConnection()

    var grossSalary by rememberSaveable { mutableStateOf("") }
    var yearlyBonus by rememberSaveable { mutableStateOf("") }
    var selectedType by rememberSaveable { mutableStateOf(TaxDefaults.taxpayerTypes.first().id) }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    var investments by rememberSaveable(stateSaver = investmentSaver) {
        mutableStateOf(TaxDefaults.investmentOptions)
    }

    val scrollState = rememberScrollState()
    val taxpayerTypes = remember { TaxDefaults.taxpayerTypes }
    val currentType = taxpayerTypes.first { it.id == selectedType }

    val salaryBreakdown = calculateSalaryBreakdown(
        grossSalary = grossSalary.toLongOrNull() ?: 0L,
        yearlyBonus = yearlyBonus.toLongOrNull() ?: 0L
    )
    val investmentRebate = calculateInvestmentRebate(investments, salaryBreakdown.taxableIncome)
    val result = calculateTax(
        income = salaryBreakdown.taxableIncome,
        taxFreeLimit = currentType.taxFreeLimit,
        investmentRebate = investmentRebate
    )
    val summary = calculateTaxSummary(
        totalIncome = salaryBreakdown.totalIncome,
        taxAfterRebate = result.taxAfterRebate
    )

    Scaffold(
        topBar = {
            AppTopBar(
                onBack = onBack,
                onReset = {
                    grossSalary = ""
                    yearlyBonus = ""
                    selectedType = taxpayerTypes.first().id
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
                .navigationBarsPadding()
        ) {
            if (showInfoDialog) {
                TaxInfoDialog(onDismiss = { showInfoDialog = false })
            }

            // Fixed Header
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                CalculatorOverviewCard(
                    currentType = currentType,
                    salaryBreakdown = salaryBreakdown,
                    taxResult = result,
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
                    onTypeSelect = { selectedType = it },
                    grossSalary = grossSalary,
                    yearlyBonus = yearlyBonus,
                    onGrossSalaryChange = { grossSalary = it },
                    onYearlyBonusChange = { yearlyBonus = it },
                )

                SalaryBreakdownCard(salaryBreakdown)

                InvestmentInputSection(
                    investments = investments,
                    onInvestmentChange = { type, value ->
                        investments = investments.map {
                            if (it.type == type) it.copy(amount = value) else it
                        }
                    }
                )

                if (salaryBreakdown.taxableIncome > currentType.taxFreeLimit) {
                    InvestmentSummaryCard(
                        taxableIncome = salaryBreakdown.taxableIncome,
                        investments = investments,
                        earnedRebate = investmentRebate
                    )
                }

                if (salaryBreakdown.totalIncome > 0) {
                    TaxSummaryCard(summary = summary)
                }

                if (salaryBreakdown.taxableIncome <= currentType.taxFreeLimit) {
                    TaxFreeCard(
                        limit = currentType.taxFreeLimit
                    )
                } else {
                    TaxResultCard(
                        result = result,
                        investmentRebate = investmentRebate
                    )
                    TaxBreakdownCard(result = result)
                }

                TaxSlabsCard()

                Text(
                    "বাংলাদেশ জাতীয় রাজস্ব বোর্ড (NBR) অনুযায়ী",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
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
                        contentDescription = "ফিরুন",
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
                    text = "আয়কর ক্যালকুলেটর",
                    fontSize = 16.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CalculatorInk,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = "করবর্ষ ${TaxDefaults.taxYearLabel}",
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
                label = "রিসেট",
                onClick = onReset
            )
            HeaderActionButton(
                icon = Icons.Default.Info,
                label = "তথ্য",
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
        contentDescription = if (expanded) "বন্ধ করুন" else "খুলুন",
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
    taxResult: TaxResult,
    totalInvestment: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp)
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
                    Text("বর্তমান সারসংক্ষেপ", fontSize = 11.sp, lineHeight = 13.sp, color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
                    Text(
                        currentType.label,
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
                        modifier = Modifier.width(104.dp),
                        label = "করযোগ্য আয়",
                        value = formatBengaliNumber(salaryBreakdown.taxableIncome),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(104.dp),
                        label = "প্রদেয় কর",
                        value = formatBengaliNumber(taxResult.taxAfterRebate.toLong()),
                        accentColor = CalculatorDanger
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(104.dp),
                        label = "বিনিয়োগ",
                        value = formatBengaliNumber(totalInvestment),
                        accentColor = CalculatorSuccess
                    )
                }
                item {
                    HeroMetricTile(
                        modifier = Modifier.width(104.dp),
                        label = "রিবেট",
                        value = formatBengaliNumber(taxResult.investmentRebate.toLong()),
                        accentColor = CalculatorInfoDark
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
        color = CalculatorSurfaceAlt,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 10.sp, lineHeight = 12.sp, color = CalculatorMuted, fontWeight = FontWeight.Medium, fontFamily = TiroBanglaFontFamily)
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
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    SectionLabel("করদাতার শ্রেণী", "আপনার করদাতার শ্রেণী নির্বাচন করুন")
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
            }
        }

        // Income input card — separate
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(20.dp)
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
private fun SectionLabel(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontSize = 15.sp, lineHeight = 10.sp, fontWeight = FontWeight.Bold, color = CalculatorInk, fontFamily = TiroBanglaFontFamily)
        Text(subtitle, fontSize = 11.sp,lineHeight = 16.sp, color = CalculatorMutedSoft, fontFamily = TiroBanglaFontFamily)
    }
}

@Composable
private fun TaxInfoDialog(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "কর নিয়ম ও তথ্য",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    "এই ক্যালকুলেটরটি ${TaxDefaults.taxYearLabel} করবর্ষের NBR নিয়ম অনুযায়ী বেতনভুক্ত ব্যক্তির স্ট্যান্ডার্ড ছাড়, চিকিৎসা ও যাতায়াত ভাতার সীমা ধরে হিসাব করে।",
                    fontSize = 14.sp,
                    color = CalculatorFieldText,
                    lineHeight = 22.sp,
                    fontFamily = TiroBanglaFontFamily
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CalculatorSuccess)
                ) {
                    Text("ঠিক আছে", modifier = Modifier.padding(vertical = 4.dp), fontFamily = TiroBanglaFontFamily)
                }
            }
        }
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
        SectionLabel("আয়ের তথ্য", "হিসাবের জন্য আপনার আয়ের তথ্য দিন")

        CurrencyInputField(
            value = grossSalary,
            onValueChange = onGrossSalaryChange,
            label = "মাসিক মূল বেতন (Basic + Others)",
            placeholder = "0"
        )

        CurrencyInputField(
            value = yearlyBonus,
            onValueChange = onYearlyBonusChange,
            label = "বছরে মোট বোনাস (উৎসব ও অন্যান্য)",
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
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = CalculatorFieldText, fontFamily = TiroBanglaFontFamily)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = CalculatorSurfaceAlt,
            border = androidx.compose.foundation.BorderStroke(1.dp, CalculatorBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (leading != null) {
                    leading()
                } else {
                    Text("৳", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = TiroBanglaFontFamily)
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(normalizeNumericInput(it)) },
                    modifier = Modifier.weight(1f),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = TiroBanglaFontFamily
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
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
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionLabel("বেতন বিভাজন", "আপনার মাসিক ও বাৎসরিক আয়ের বিস্তারিত")

            DetailPanel(
                title = "মাসিক বিভাজন",
                color = MaterialTheme.colorScheme.primary,
                expanded = isMonthlyExpanded,
                onToggle = { isMonthlyExpanded = !isMonthlyExpanded },
                summaryContent = {
                    SalaryItem("মোট মাসিক", salary.grossSalary, isTotal = true)
                }
            ) {
                SalaryItem("মূল বেতন (৬০%)", salary.basicSalary)
                SalaryItem("বাড়ি ভাড়া (২৫%)", salary.houseRent)
                SalaryItem("চিকিৎসা (১০%)", salary.medical)
                SalaryItem("যাতায়াত (৫%)", salary.conveyance)
            }

            DetailPanel(
                title = "বাৎসরিক বিশ্লেষণ",
                color = CalculatorInfo,
                expanded = isYearlyExpanded,
                onToggle = { isYearlyExpanded = !isYearlyExpanded },
                summaryContent = {
                    YearlyItem("নিট করযোগ্য আয়", salary.taxableIncome, isTotal = true)
                }
            ) {
                YearlyItem("মোট বাৎসরিক বেতন", salary.totalIncome)
                YearlyItem("মোট বোনাস", salary.yearlyBonus)
                YearlyItem("স্ট্যান্ডার্ড ছাড়", salary.totalExemption, isExempt = true)
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
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(modifier = Modifier.size(5.dp, 14.dp).background(color, RoundedCornerShape(2.dp)))
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = TiroBanglaFontFamily)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                if (expanded) "কম দেখুন" else "বিস্তারিত",
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
            border = androidx.compose.foundation.BorderStroke(1.dp, CalculatorDivider)
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
private fun TaxSummaryCard(summary: TaxSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("ট্যাক্স দক্ষতা সারসংক্ষেপ", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = TiroBanglaFontFamily)
            SummaryRow("কার্যকর কর হার", "${summary.effectiveTaxRatePercent}%", MaterialTheme.colorScheme.onPrimary, CalculatorPositive)
            SummaryRow("মাসিক হাতে থাকবে", formatBengaliNumber(summary.yearlyNetIncomeAfterTax / 12), MaterialTheme.colorScheme.onPrimary, MaterialTheme.colorScheme.onPrimary)
            SummaryRow("মাসিক কর", formatBengaliNumber(summary.monthlyTaxEstimate), MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), CalculatorDangerSoft)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, labelColor: Color, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = labelColor, fontSize = 13.sp, fontFamily = TiroBanglaFontFamily)
        Text(value, color = valueColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = TiroBanglaFontFamily)
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
        shape = RoundedCornerShape(20.dp),
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
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("বিনিয়োগ রেয়াত", "আপনার বিনিয়োগ তথ্য দিন")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        if (isExpanded) "কম দেখুন" else "বিনিয়োগ যোগ করুন",
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
                            label = investment.type,
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
    
    // Max investment required to get max rebate: maxRebateLimit / 0.15
    val maxInvestmentRequired = (maxRebateLimit / TaxDefaults.investmentRebateRate).toLong()
    val progress = if (maxRebateLimit > 0) (earnedRebate / maxRebateLimit).toFloat().coerceIn(0f, 1f) else 0f
    val remainingInvestmentNeeded = maxOf(0L, maxInvestmentRequired - totalInvestment)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
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
                    "বিনিয়োগ ও রিবেট",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CalculatorInk,
                    fontFamily = TiroBanglaFontFamily
                )
                Icon(
                    Icons.Default.Verified,
                    contentDescription = null,
                    tint = CalculatorInk,
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
                        "রিবেট সীমা ব্যবহার",
                        fontSize = 14.sp,
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
                        .graphicsLayer {
                            shape = RoundedCornerShape(5.dp)
                            clip = true
                        },
                    color = CalculatorSuccess,
                    trackColor = CalculatorSurfaceAlt,
                )
            }

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InvestmentStatBox(
                    modifier = Modifier.weight(1f),
                    label = "মোট বিনিয়োগ",
                    value = formatBengaliNumber(totalInvestment)
                )
                InvestmentStatBox(
                    modifier = Modifier.weight(1f),
                    label = "অর্জিত রিবেট",
                    value = formatBengaliNumber(earnedRebate.toLong())
                )
            }

            // Tip Section
            if (remainingInvestmentNeeded > 0) {
                Text(
                    "টিপস: আরও ${formatBengaliNumber(remainingInvestmentNeeded)} টাকা বিনিয়োগ করলে রিবেট সর্বোচ্চ সীমায় পৌঁছাবে।",
                    fontSize = 13.sp,
                    color = CalculatorMuted,
                    lineHeight = 20.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            } else {
                Text(
                    "অভিনন্দন! আপনি আপনার আয়ের বিপরীতে সর্বোচ্চ রিবেট সীমা অর্জন করেছেন।",
                    fontSize = 13.sp,
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
                "৳ $value",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CalculatorInk,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun TaxResultCard(result: TaxResult, investmentRebate: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.onBackground, CalculatorInk)
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("চূড়ান্ত প্রদেয় কর", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f), fontSize = 12.sp, fontFamily = TiroBanglaFontFamily)
                Text(formatBengaliNumber(result.taxAfterRebate.toLong()), color = MaterialTheme.colorScheme.onPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, fontFamily = TiroBanglaFontFamily)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))

            Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                ResultRow("মোট কর", formatBengaliNumber(result.totalTax.toLong()))
                ResultRow("বিনিয়োগ রিবেট", "- ${formatBengaliNumber(investmentRebate.toLong())}", color = CalculatorPositive)
                HorizontalDivider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f))
                ResultRow("নিট প্রদেয়", formatBengaliNumber(result.taxAfterRebate.toLong()), isBold = true)
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String, color: Color? = null, isBold: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val valueColor = color ?: MaterialTheme.colorScheme.onPrimary
        Text(label, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f), fontSize = 13.sp, fontFamily = TiroBanglaFontFamily)
        Text(value, color = valueColor, fontSize = 15.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium, fontFamily = TiroBanglaFontFamily)
    }
}

@Composable
private fun TaxBreakdownCard(result: TaxResult) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CalculatorDivider)
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
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel("কর ধাপ অনুযায়ী হিসাব", "আপনার আয়ের ওপর ট্যাক্স যেভাবে বসানো হয়েছে")
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
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(slab.label, fontSize = 11.sp, color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
                Text("${slab.rate}% হার", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontFamily = TiroBanglaFontFamily)
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
        shape = RoundedCornerShape(18.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (isSelected) CalculatorSuccess.copy(alpha = 0.08f) else CalculatorSurfaceAlt,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) CalculatorSuccess else CalculatorBorder
                )
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
                    type.label,
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
                    "করমুক্ত: ${formatBengaliNumber(type.taxFreeLimit / 1000)}k",
                    fontSize = 9.sp,
                    lineHeight = 10.sp,
                    color = if (isSelected) CalculatorSuccess else CalculatorMutedSoft,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            if (isSelected) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(15.dp),
                    shape = CircleShape,
                    color = CalculatorSuccess
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(2.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TaxFreeCard(limit: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorInfoBackground),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Rule, contentDescription = null, tint = CalculatorInfo, modifier = Modifier.size(26.dp))
            Column {
                Text("কর প্রযোজ্য নয়", fontWeight = FontWeight.Bold, color = CalculatorInfoDark, fontFamily = TiroBanglaFontFamily)
                Text(
                    "আপনার আয় ${formatBengaliNumber(limit)} টাকার করমুক্ত সীমার মধ্যে আছে।",
                    fontSize = 12.sp,
                    color = CalculatorInfo,
                    lineHeight = 17.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun TaxSlabsCard() {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorSurfaceAlt),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CalculatorBorder)
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
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(18.dp))
                    Text("স্ট্যান্ডার্ড কর ধাপ (${TaxDefaults.taxYearLabel})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontFamily = TiroBanglaFontFamily)
                }
                AnimatedChevron(
                    expanded = isExpanded,
                    tint = CalculatorMuted,
                )
            }

            ExpandableContent(expanded = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaxDefaults.taxSlabs.forEach { (label, rate) ->
                        SlabInfoRow(label, rate)
                    }
                }
            }
        }
    }
}

@Composable
private fun SlabInfoRow(label: String, rate: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
        Text(rate, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, fontFamily = TiroBanglaFontFamily)
    }
}
