package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.audit.AuditDataset
import com.maruf.bdtaxcalculator.audit.AuditLookupResult
import com.maruf.bdtaxcalculator.audit.AuditRepository
import com.maruf.bdtaxcalculator.audit.maskTin
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDangerSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDangerSoft2
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientBottom
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientTop
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfo
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInk
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AuditCheckerScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val hideKeyboardOnScrollConnection = rememberKeyboardDismissOnScrollConnection()

    var tinInput by rememberSaveable { mutableStateOf("") }
    var dataset by remember { mutableStateOf<AuditDataset?>(null) }
    var lookupResult by remember { mutableStateOf<AuditLookupResult?>(null) }
    var hasSearched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dataset = withContext(Dispatchers.IO) {
            AuditRepository.load(context)
        }
    }

    Scaffold(
        topBar = {
            AuditTopBar(
                onBack = onBack,
                onReset = {
                    tinInput = ""
                    lookupResult = null
                    hasSearched = false
                }
            )
        },
        containerColor = CalculatorBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(CalculatorGradientTop, CalculatorGradientMiddle, CalculatorGradientBottom)
                    )
                )
                .imePadding()
                .navigationBarsPadding()
        ) {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                AuditSearchCard(
                    tinInput = tinInput,
                    dataset = dataset,
                    canSearch = tinInput.length == TinInputLength && dataset != null,
                    onTinChange = { value ->
                        tinInput = normalizeAuditTinInput(value)
                        lookupResult = null
                        hasSearched = false
                    },
                    onSearch = {
                        lookupResult = dataset?.lookupTin(tinInput)
                        hasSearched = true
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(hideKeyboardOnScrollConnection)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (hasSearched) {
                    AuditLookupResultCard(result = lookupResult, searchedTin = tinInput)
                }

                AuditDatasetSummary(dataset = dataset)

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
private fun AuditTopBar(
    onBack: (() -> Unit)?,
    onReset: () -> Unit
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
                HeaderIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = "ফিরুন",
                    onClick = onBack
                )
            }
            Text(
                "অডিট চেক",
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CalculatorInk,
                fontFamily = TiroBanglaFontFamily
            )
        }

        HeaderIconButton(
            icon = Icons.Default.Refresh,
            label = "রিসেট",
            onClick = onReset
        )
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
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
private fun AuditSearchCard(
    tinInput: String,
    dataset: AuditDataset?,
    canSearch: Boolean,
    onTinChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CalculatorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "TIN Audit Search",
                    color = CalculatorInk,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    "১২ সংখ্যার TIN লিখুন। সার্চ সম্পূর্ণ অফলাইন এবং ডেটা অ্যাপের বাইরে কোথাও যায় না।",
                    color = CalculatorMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            AuditTinInput(
                value = tinInput,
                onValueChange = onTinChange,
                onSearch = onSearch
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${tinInput.length}/12 digits",
                    color = CalculatorMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = TiroBanglaFontFamily
                )
                AuditStatusPill(
                    text = dataset?.let { "Ready · ${formatBengaliNumber(it.totalRecords.toLong())} records" } ?: "লোড হচ্ছে...",
                    background = CalculatorSuccess.copy(alpha = 0.1f),
                    color = CalculatorSuccess
                )
            }

            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = canSearch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CalculatorSuccess,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = HomeSoftGreen,
                    disabledContentColor = HomeTextMuted
                )
            ) {
                Text(
                    "Check Audit Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun AuditTinInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CalculatorSurfaceAlt,
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = CalculatorSuccess,
                modifier = Modifier.size(19.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(normalizeAuditTinInput(it)) },
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalculatorInk,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (value.length == TinInputLength) {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onSearch()
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            "১২-সংখ্যার TIN লিখুন",
                            color = CalculatorMuted.copy(alpha = 0.72f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun AuditDatasetSummary(dataset: AuditDataset?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "ডেটাসেট সামারাইজেশন",
            color = CalculatorInk,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "মোট TIN",
                value = dataset?.totalRecords,
                pillBackground = CalculatorSuccess.copy(alpha = 0.1f),
                pillColor = CalculatorSuccess
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "নির্বাচিত",
                value = dataset?.selectedCount,
                pillBackground = CalculatorDangerSoft2,
                pillColor = CalculatorDanger
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "নির্বাচিত নয়",
                value = dataset?.notSelectedCount,
                pillBackground = CalculatorSuccess.copy(alpha = 0.1f),
                pillColor = CalculatorSuccess
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "ট্যাক্স জোন",
                value = dataset?.zoneCount,
                pillBackground = CalculatorInfoBackground,
                pillColor = CalculatorInfo
            )
        }
    }
}

@Composable
private fun AuditSummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    value: Int?,
    pillBackground: Color,
    pillColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AuditStatusPill(
                text = label,
                background = pillBackground,
                color = pillColor
            )
            Text(
                value?.let { formatBengaliNumber(it.toLong()) } ?: "—",
                color = CalculatorInk,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun AuditStatusPill(
    text: String,
    background: Color,
    color: Color
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun AuditLookupResultCard(result: AuditLookupResult?, searchedTin: String) {
    val isAudit = result?.isSelected == true
    val statusColor = when {
        result == null -> CalculatorMuted
        isAudit -> CalculatorDanger
        else -> CalculatorSuccess
    }
    val statusTitle = when {
        result == null -> "রেকর্ড পাওয়া যায়নি"
        isAudit -> "অডিটের জন্য নির্বাচিত"
        else -> "অডিট তালিকায় নেই"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(color = statusColor.copy(alpha = 0.1f), shape = CircleShape) {
                Icon(
                    if (result == null) Icons.Default.Info else if (isAudit) Icons.AutoMirrored.Filled.Rule else Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(28.dp),
                    tint = statusColor
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    statusTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = statusColor,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    result?.let { "TIN ${maskTin(it.tin)} · করবর্ষ: ${it.assessmentYear}" }
                        ?: "TIN ${maskTin(searchedTin)} বর্তমান ডেটাসেটে পাওয়া যায়নি।",
                    fontSize = 12.sp,
                    color = statusColor.copy(alpha = 0.78f),
                    fontFamily = TiroBanglaFontFamily,
                    textAlign = TextAlign.Center
                )
            }

            if (result != null) {
                HorizontalDivider(color = statusColor.copy(alpha = 0.1f))
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AuditDetailRow("জোন", result.zone, statusColor)
                    AuditDetailRow("সার্কেল", result.circle, statusColor)
                    AuditDetailRow("ধরণ", result.category, statusColor)
                }
            }
        }
    }
}

@Composable
private fun AuditDetailRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 12.sp, color = color.copy(alpha = 0.7f), fontFamily = TiroBanglaFontFamily)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, fontFamily = TiroBanglaFontFamily)
    }
}
