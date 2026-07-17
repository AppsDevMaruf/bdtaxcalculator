package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.platform.LocalUriHandler
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
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDangerSoft2
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientBottom
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientTop
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfo
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInk
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AuditCheckerScreen(
    onBack: (() -> Unit)? = null,
    onRequestInAppReview: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val uriHandler = LocalUriHandler.current
    val hideKeyboardOnScrollConnection = rememberKeyboardDismissOnScrollConnection()

    var tinInput by rememberSaveable { mutableStateOf("") }
    var dataset by remember { mutableStateOf<AuditDataset?>(null) }
    var hasSearched by rememberSaveable { mutableStateOf(false) }
    val lookupResult = if (hasSearched) dataset?.lookupTin(tinInput) else null

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
                        hasSearched = false
                    },
                    onSearch = {
                        hasSearched = true
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        FirebaseTracker.logEvent("audit_search_completed")
                        onRequestInAppReview("audit_search_completed")
                    }
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(hideKeyboardOnScrollConnection)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = 16.dp,
                            top = 4.dp,
                            end = 16.dp,
                            bottom = FloatingBottomBarSafePadding
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (hasSearched) {
                    AuditLookupResultCard(result = lookupResult, searchedTin = tinInput)
                }

                AuditGuidanceCard(
                    hasSearched = hasSearched,
                    result = lookupResult,
                    onOpenNbr = { uriHandler.openUri(NbrWebsiteUrl) },
                    onOpenEReturn = { uriHandler.openUri(EReturnWebsiteUrl) }
                )

                AuditDatasetSummary(dataset = dataset)
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
                    label = localizedText("ফিরুন", "Back"),
                    onClick = onBack
                )
            }
            Text(
                localizedText("অডিট চেক", "Audit Check"),
                fontSize = 16.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CalculatorInk,
                fontFamily = TiroBanglaFontFamily
            )
        }

        HeaderIconButton(
            icon = Icons.Default.Refresh,
            label = localizedText("রিসেট", "Reset"),
            onClick = onReset
        )
    }
}

@Composable
private fun HeaderIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
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
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CalculatorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    localizedText("TIN Audit Search", "TIN Audit Search"),
                    color = CalculatorInk,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        "১২ সংখ্যার TIN লিখুন। সার্চ সম্পূর্ণ অফলাইন এবং ডেটা অ্যাপের বাইরে কোথাও যায় না। Assessment year 23-24",
                        "Enter your 12-digit TIN. Search runs fully offline and no data leaves the app. Assessment year 2023-24"
                    ),
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
                    localizedText("${formatBengaliNumber(tinInput.length.toLong())}/১২ ডিজিট", "${tinInput.length}/12 digits"),
                    color = CalculatorMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = TiroBanglaFontFamily
                )
                AuditStatusPill(
                    text = dataset?.let {
                        localizedText(
                            "প্রস্তুত · ${formatBengaliNumber(it.totalRecords.toLong())} রেকর্ড",
                            "Ready · ${formatBengaliNumber(it.totalRecords.toLong())} records"
                        )
                    } ?: localizedText("লোড হচ্ছে...", "Loading..."),
                    background = CalculatorSuccess.copy(alpha = 0.1f),
                    color = CalculatorSuccess
                )
            }

            Button(
                onClick = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = canSearch,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CalculatorSuccess,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = HomeSoftGreen,
                    disabledContentColor = HomeTextMuted
                )
            ) {
                Text(
                    localizedText("অডিট স্ট্যাটাস দেখুন", "Check Audit Status"),
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
                    fontWeight = FontWeight.Medium,
                    color = CalculatorInk,
                    fontFamily = TiroBanglaFontFamily
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
                            localizedText("১২-সংখ্যার TIN লিখুন", "Enter 12-digit TIN"),
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
private fun AuditGuidanceCard(
    hasSearched: Boolean,
    result: AuditLookupResult?,
    onOpenNbr: () -> Unit,
    onOpenEReturn: () -> Unit
) {
    val resultGuidance = when {
        !hasSearched -> localizedText(
            "TIN সার্চ করলে এখানে আপনার পরবর্তী করণীয় দেখাবে।",
            "Search a TIN to see what to do next."
        )

        result?.isSelected == true -> localizedText(
            "নির্বাচিত হলে রিটার্ন acknowledgement, আয়-ব্যয়ের কাগজ এবং বিনিয়োগ প্রমাণ প্রস্তুত রাখুন; প্রয়োজনে সংশ্লিষ্ট সার্কেল অফিসে যোগাযোগ করুন।",
            "If selected, keep your return acknowledgement, income-expense papers, and investment proof ready; contact the relevant circle office if needed."
        )

        else -> localizedText(
            "রেকর্ড না পাওয়া বা selected না হলে এই static audit list-এ আপনার TIN নেই। তবুও কোনো নোটিশ পেলে official NBR/e-Return বা সংশ্লিষ্ট সার্কেল থেকে যাচাই করুন।",
            "If not found or not selected, your TIN is not in this static audit list. If you receive any notice, verify it through official NBR/e-Return or your circle office."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CalculatorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                localizedText("পরবর্তী করণীয় ও সহায়তা", "Next Steps and Support"),
                color = CalculatorInk,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )

            GuidanceBullet(resultGuidance)
            GuidanceBullet(
                localizedText(
                    "জিরো রিটার্ন বা audit status নিয়ে সন্দেহ থাকলে e-Return hotline 09643717171 অথবা official NBR website ব্যবহার করুন।",
                    "For zero return or audit-status concerns, use the e-Return hotline 09643717171 or the official NBR website."
                )
            )
            GuidanceBullet(
                localizedText(
                    "অফিশিয়াল লিখিত নোটিশ ছাড়া কারও কাছে password, OTP, NID copy বা টাকা পাঠাবেন না। প্রয়োজনে কর্মকর্তা/সার্কেল NBR website থেকে যাচাই করুন।",
                    "Do not share passwords, OTPs, NID copies, or money without an official written notice. Verify officers/circles from the NBR website when needed."
                )
            )

            HorizontalDivider(color = CalculatorBorder)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GuidanceLinkChip(
                    modifier = Modifier.weight(1f),
                    text = localizedText("NBR ওয়েবসাইট", "NBR Website"),
                    onClick = onOpenNbr
                )
                GuidanceLinkChip(
                    modifier = Modifier.weight(1f),
                    text = localizedText("e-Return", "e-Return"),
                    onClick = onOpenEReturn
                )
            }
        }
    }
}

@Composable
private fun GuidanceBullet(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp),
            shape = CircleShape,
            color = CalculatorSuccess
        ) {}
        Text(
            text,
            modifier = Modifier.weight(1f),
            color = CalculatorMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun GuidanceLinkChip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = CalculatorSuccess.copy(alpha = 0.1f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, CalculatorSuccess.copy(alpha = 0.35f))
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            color = CalculatorSuccess,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun AuditDatasetSummary(dataset: AuditDataset?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            localizedText("ডেটাসেট সামারাইজেশন", "Dataset Summary"),
            color = CalculatorInk,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = localizedText("মোট TIN", "Total TIN"),
                value = dataset?.totalRecords,
                pillBackground = CalculatorSuccess.copy(alpha = 0.1f),
                pillColor = CalculatorSuccess
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = localizedText("নির্বাচিত", "Selected"),
                value = dataset?.selectedCount,
                pillBackground = CalculatorDangerSoft2,
                pillColor = CalculatorDanger
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = localizedText("নির্বাচিত নয়", "Not Selected"),
                value = dataset?.notSelectedCount,
                pillBackground = CalculatorSuccess.copy(alpha = 0.1f),
                pillColor = CalculatorSuccess
            )
            AuditSummaryCard(
                modifier = Modifier.weight(1f),
                label = localizedText("ট্যাক্স জোন", "Tax Zone"),
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
        shape = RoundedCornerShape(10.dp),
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
        shape = RoundedCornerShape(8.dp)
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
        result == null -> localizedText("রেকর্ড পাওয়া যায়নি", "Record Not Found")
        isAudit -> localizedText("অডিটের জন্য নির্বাচিত", "Selected for Audit")
        else -> localizedText("অডিট তালিকায় নেই", "Not in Audit List")
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
                    result?.let {
                        localizedText(
                            "TIN ${maskTin(it.tin)} · করবর্ষ: ${it.assessmentYear}",
                            "TIN ${maskTin(it.tin)} · Assessment year: ${it.assessmentYear}"
                        )
                    } ?: localizedText(
                        "TIN ${maskTin(searchedTin)} বর্তমান ডেটাসেটে পাওয়া যায়নি।",
                        "TIN ${maskTin(searchedTin)} was not found in the current dataset."
                    ),
                    fontSize = 12.sp,
                    color = statusColor.copy(alpha = 0.78f),
                    fontFamily = TiroBanglaFontFamily,
                    textAlign = TextAlign.Center
                )
            }

            if (result != null) {
                HorizontalDivider(color = statusColor.copy(alpha = 0.1f))
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AuditDetailRow(localizedText("জোন", "Zone"), result.zone, statusColor)
                    AuditDetailRow(localizedText("সার্কেল", "Circle"), result.circle, statusColor)
                    AuditDetailRow(localizedText("ধরণ", "Type"), result.category, statusColor)
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

private const val NbrWebsiteUrl = "https://nbr.gov.bd"
private const val EReturnWebsiteUrl = "https://etaxnbr.gov.bd"
