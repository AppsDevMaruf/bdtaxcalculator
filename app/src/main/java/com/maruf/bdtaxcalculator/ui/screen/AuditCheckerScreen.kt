package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.maruf.bdtaxcalculator.ui.theme.AuditDanger
import com.maruf.bdtaxcalculator.ui.theme.AuditDisabledButton
import com.maruf.bdtaxcalculator.ui.theme.AuditDisabledText
import com.maruf.bdtaxcalculator.ui.theme.AuditInputBackground
import com.maruf.bdtaxcalculator.ui.theme.AuditReadyPill
import com.maruf.bdtaxcalculator.ui.theme.AuditReadyText
import com.maruf.bdtaxcalculator.ui.theme.AuditScreenBackground
import com.maruf.bdtaxcalculator.ui.theme.AuditSelectedPill
import com.maruf.bdtaxcalculator.ui.theme.AuditSelectedText
import com.maruf.bdtaxcalculator.ui.theme.AuditZonePill
import com.maruf.bdtaxcalculator.ui.theme.AuditZoneText
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AuditCheckerScreen(onBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var tinInput by rememberSaveable { mutableStateOf("") }
    var auditDataset by remember { mutableStateOf<AuditDataset?>(null) }
    var lookupResult by remember { mutableStateOf<AuditLookupResult?>(null) }
    var hasSearched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        auditDataset = withContext(Dispatchers.IO) {
            AuditRepository.load(context)
        }
    }

    val dataset = auditDataset
    val canSearch = tinInput.length == 12 && dataset != null

    Scaffold(
        topBar = {
            AuditTopBar(onBack = onBack ?: {})

        },
        //modifier = Modifier.safeDrawingPadding(),
        containerColor = AuditScreenBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AuditScreenBackground)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AuditSearchCard(
                tinInput = tinInput,
                dataset = dataset,
                canSearch = canSearch,
                onTinChange = { value ->
                    tinInput = value.filter(Char::isDigit).take(12)
                    lookupResult = null
                    hasSearched = false
                },
                onSearch = {
                    lookupResult = dataset?.lookupTin(tinInput)
                    hasSearched = true
                    keyboardController?.hide()
                }
            )

            if (hasSearched) {
                AuditLookupResultCard(result = lookupResult, searchedTin = tinInput)
            }

            AuditDatasetSummary(dataset = dataset)

            Text(
                "বাংলাদেশ জাতীয় রাজস্ব বোর্ড (NBR) অনুযায়ী",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                color = HomeTextMuted,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun AuditTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()+8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Row(
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "ফিরুন",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "ফিরুন",
                    fontSize = 12.sp,
                    color = HomeTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = TiroBanglaFontFamily
                )
            }
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
        border = BorderStroke(1.dp, HomeBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "TIN Audit Search",
                    color = HomeTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    "১২ সংখ্যার TIN লিখুন। সার্চ সম্পূর্ণ অফলাইন এবং ডেটা অ্যাপের বাইরে কোথাও যায় না।",
                    color = HomeTextMuted,
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
                    color = HomeTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = TiroBanglaFontFamily
                )
                AuditStatusPill(
                    text = dataset?.let { "Ready · ${formatBengaliNumber(it.totalRecords.toLong())} records" } ?: "লোড হচ্ছে...",
                    background = AuditReadyPill,
                    color = AuditReadyText
                )
            }

            Button(
                onClick = onSearch,
                modifier = Modifier.fillMaxWidth(),
                enabled = canSearch,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = AuditDisabledButton,
                    disabledContentColor = AuditDisabledText
                )
            ) {
                Text(
                    "Check Audit Status",
                    modifier = Modifier.padding(vertical = 7.dp),
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
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = AuditInputBackground,
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(19.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (value.length == 12) {
                            onSearch()
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            "১২-সংখ্যার TIN লিখুন",
                            color = HomeTextMuted.copy(alpha = 0.72f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
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
            color = HomeTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "মোট TIN",
                value = dataset?.totalRecords,
                pillBackground = AuditReadyPill,
                pillColor = AuditReadyText
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "নির্বাচিত",
                value = dataset?.selectedCount,
                pillBackground = AuditSelectedPill,
                pillColor = AuditSelectedText
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "নির্বাচিত নয়",
                value = dataset?.notSelectedCount,
                pillBackground = AuditReadyPill,
                pillColor = AuditReadyText
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = "ট্যাক্স জোন",
                value = dataset?.zoneCount,
                pillBackground = AuditZonePill,
                pillColor = AuditZoneText
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
                color = HomeTextPrimary,
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
        result == null -> HomeTextMuted
        isAudit -> AuditDanger
        else -> MaterialTheme.colorScheme.primary
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
