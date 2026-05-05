package com.maruf.bdtaxcalculator.ui.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.tax.LocalTaxPreferenceStore
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoDark
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import androidx.core.net.toUri

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    language: String,
    themeMode: String,
    onLanguageChange: (String) -> Unit,
    onThemeModeChange: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTaxpayerType by remember {
        mutableStateOf(LocalTaxPreferenceStore.getDefaultTaxpayerType(context))
    }
    var selectedAssessmentType by remember {
        mutableStateOf(LocalTaxPreferenceStore.getAssessmentType(context))
    }
    var showLanguageSheet by remember { mutableStateOf(false) }
    val languageSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val appVersion = remember(context.packageName) { context.getAppVersionName() }

    if (showLanguageSheet) {
        LanguageBottomSheet(
            language = language,
            sheetState = languageSheetState,
            onDismiss = { showLanguageSheet = false },
            onLanguageChange = { selectedLanguage ->
                onLanguageChange(selectedLanguage)
                showLanguageSheet = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        SettingsHeader()

        Spacer(modifier = Modifier.size(20.dp))

        PrivacyHeroCard()

        Spacer(modifier = Modifier.size(20.dp))

        AppLanguageRowCard(
            language = language,
            onClick = { showLanguageSheet = true }
        )

        Spacer(modifier = Modifier.size(18.dp))

        ThemeToggleCard(
            isDarkMode = themeMode == AppUiPreferences.themeDark,
            onThemeChange = { isDark ->
                onThemeModeChange(
                    if (isDark) AppUiPreferences.themeDark else AppUiPreferences.themeLight
                )
            }
        )

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = localizedText("লোকাল ট্যাক্স সেটআপ", "Local tax setup")) {
            Text(
                localizedText(
                    "নতুন হিসাব শুরু হলে এই সেটিংস ডিফল্ট হিসেবে ব্যবহার হবে।",
                    "These settings are used as defaults for new calculations."
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = CalculatorMuted,
                fontFamily = TiroBanglaFontFamily
            )

            TaxDefaults.taxpayerTypes.forEach { type ->
                PreferenceChoiceRow(
                    icon = type.icon ?: Icons.Default.Person,
                    title = type.label,
                    subtitle = localizedText(
                        "করমুক্ত সীমা: ৳ ${formatBengaliNumber(type.taxFreeLimit)}",
                        "Tax-free limit: BDT ${type.taxFreeLimit}"
                    ),
                    selected = selectedTaxpayerType == type.id,
                    onClick = {
                        selectedTaxpayerType = type.id
                        LocalTaxPreferenceStore.setDefaultTaxpayerType(context, type.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = localizedText("অ্যাসেসমেন্ট ধরন", "Assessment type")) {
            PreferenceChoiceRow(
                icon = Icons.Default.CheckCircle,
                title = localizedText("সাধারণ অ্যাসেসমেন্ট", "Regular assessment"),
                subtitle = localizedText(
                    "ন্যূনতম কর: ৳ ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())}",
                    "Minimum tax: BDT ${TaxDefaults.minimumTax.toLong()}"
                ),
                selected = selectedAssessmentType == LocalTaxPreferenceStore.assessmentRegular,
                onClick = {
                    selectedAssessmentType = LocalTaxPreferenceStore.assessmentRegular
                    LocalTaxPreferenceStore.setAssessmentType(context, LocalTaxPreferenceStore.assessmentRegular)
                }
            )
            PreferenceChoiceRow(
                icon = Icons.Default.Calculate,
                title = localizedText("নতুন অ্যাসেসমেন্ট", "New assessment"),
                subtitle = localizedText(
                    "ন্যূনতম কর: ৳ ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}",
                    "Minimum tax: BDT ${TaxDefaults.newAssessmentMinimumTax.toLong()}"
                ),
                selected = selectedAssessmentType == LocalTaxPreferenceStore.assessmentNew,
                onClick = {
                    selectedAssessmentType = LocalTaxPreferenceStore.assessmentNew
                    LocalTaxPreferenceStore.setAssessmentType(context, LocalTaxPreferenceStore.assessmentNew)
                }
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = localizedText("প্রাইভেসি ও ডেটা", "Privacy & data")) {
            InfoRow(
                icon = Icons.Default.Lock,
                title = localizedText("অফলাইন ও প্রাইভেট", "Offline and private"),
                subtitle = localizedText(
                    "কোনো লগইন, সার্ভার, ডেটাবেস বা ইনপুট সংরক্ষণ নেই।",
                    "No login, server, database, or saved tax input."
                )
            )
            InfoRow(
                icon = Icons.Default.Info,
                title = localizedText("রুলস ডেটা", "Rules data"),
                subtitle = localizedText(
                    "করবর্ষ ${TaxDefaults.taxYearLabel}; অডিট ডেটা অ্যাপের static JSON থেকে লোড হয়।",
                    "Tax year ${TaxDefaults.taxYearLabel}; audit data loads from static JSON inside the app."
                )
            )
            InfoRow(
                icon = Icons.Default.Settings,
                title = localizedText("শুধু লোকাল পছন্দ", "Local preferences only"),
                subtitle = localizedText(
                    "ডিফল্ট করদাতা ও অ্যাসেসমেন্ট ধরন শুধু এই ডিভাইসে থাকে।",
                    "Default taxpayer and assessment choices stay only on this device."
                )
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = localizedText("সহায়তা", "Support")) {
            ActionRow(
                icon = Icons.Default.Share,
                title = localizedText("অ্যাপ শেয়ার করুন", "Share app"),
                subtitle = localizedText(
                    "Tax Calculator BD অন্যদের জানাতে শেয়ার করুন।",
                    "Share Tax Calculator BD with others."
                ),
                onClick = { context.shareApp() }
            )
            ActionRow(
                icon = Icons.Default.Star,
                title = localizedText("রেট দিন", "Rate app"),
                subtitle = localizedText("Play Store-এ রেটিং দিন।", "Rate us on the Play Store."),
                onClick = { context.openPlayStore() }
            )
            ActionRow(
                icon = Icons.Default.Email,
                title = localizedText("সমস্যা জানান", "Report issue"),
                subtitle = localizedText(
                    "ভুল হিসাব বা ডেটা সমস্যা জানাতে ইমেইল করুন।",
                    "Email us about calculation or data issues."
                ),
                onClick = { context.sendFeedbackEmail() }
            )
            ActionRow(
                icon = Icons.Default.Delete,
                title = localizedText("লোকাল সেটিংস রিসেট", "Reset local settings"),
                subtitle = localizedText(
                    "ডিফল্ট করদাতা ও অ্যাসেসমেন্ট সেটিংস পরিষ্কার করুন।",
                    "Clear default taxpayer and assessment settings."
                ),
                onClick = {
                    LocalTaxPreferenceStore.clear(context)
                    selectedTaxpayerType = LocalTaxPreferenceStore.getDefaultTaxpayerType(context)
                    selectedAssessmentType = LocalTaxPreferenceStore.getAssessmentType(context)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
            localizedText(
                "বাংলাদেশ জাতীয় রাজস্ব বোর্ড (NBR) অনুযায়ী",
                "Based on Bangladesh National Board of Revenue (NBR) rules"
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            color = CalculatorMuted,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            localizedText("সংস্করণ $appVersion", "Version $appVersion"),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 12.sp,
            color = CalculatorMutedSoft,
            fontFamily = TiroBanglaFontFamily
        )

        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column {
                Text(
                    localizedText("সেটিংস", "Settings"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        "প্রাইভেসি-ফার্স্ট লোকাল কনফিগারেশন",
                        "Privacy-first local configuration"
                    ),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun PrivacyHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = CircleShape, color = HomeSoftBlue) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp).size(22.dp),
                        tint = HomeActionBlue
                    )
                }
                Column {
                    Text(
                        localizedText("আপনার ডেটা আপনার ডিভাইসে", "Your data stays on your device"),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextPrimary,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Text(
                        localizedText(
                            "Tax Calculator BD কোনো ব্যক্তিগত প্রোফাইল তৈরি করে না।",
                            "Tax Calculator BD does not create a personal profile."
                        ),
                        fontSize = 13.sp,
                        color = CalculatorMuted,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPill(localizedText("নো লগইন", "No login"))
                PrivacyPill(localizedText("নো সার্ভার", "No server"))
                PrivacyPill(localizedText("নো ইনপুট সেভ", "No input save"))
            }
        }
    }
}

@Composable
private fun PrivacyPill(label: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = HomeSoftGreen,
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 11.sp,
            color = CalculatorSuccess,
            fontWeight = FontWeight.Bold,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun AppLanguageRowCard(language: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(color = HomeSoftBlue, shape = RoundedCornerShape(14.dp)) {
                Text(
                    if (language == AppUiPreferences.languageEnglish) "\uD83C\uDDFA\uD83C\uDDF8" else "\uD83C\uDDE7\uD83C\uDDE9",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedText("অ্যাপের ভাষা", "App Language"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    if (language == AppUiPreferences.languageEnglish) "English" else "বাংলা",
                    fontSize = 12.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = CalculatorMutedSoft
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun LanguageBottomSheet(
    language: String,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
                    .size(width = 48.dp, height = 5.dp),
                shape = RoundedCornerShape(999.dp),
                color = HomeBorder
            ) {}
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                Text(
                    localizedText("অ্যাপের ভাষা", "App Language"),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        "ভাষা পুরো অ্যাপে প্রয়োগ হবে",
                        "Language changes will be applied to the entire app"
                    ),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            LanguageOptionRow(
                flag = "\uD83C\uDDFA\uD83C\uDDF8",
                title = "English",
                selected = language == AppUiPreferences.languageEnglish,
                onClick = { onLanguageChange(AppUiPreferences.languageEnglish) }
            )
            LanguageOptionRow(
                flag = "\uD83C\uDDE7\uD83C\uDDE9",
                title = "বাংলা",
                selected = language == AppUiPreferences.languageBangla,
                onClick = { onLanguageChange(AppUiPreferences.languageBangla) }
            )
        }
    }
}

@Composable
private fun LanguageOptionRow(
    flag: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            flag,
            fontSize = 20.sp
        )
        Text(
            title,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = HomeTextPrimary,
            fontFamily = TiroBanglaFontFamily
        )
        if (selected) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = CalculatorSuccess
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ThemeToggleCard(
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = if (isDarkMode) HomeSoftBlue else HomeSoftGreen,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    if (isDarkMode) Icons.Default.DarkMode else Icons.Default.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = if (isDarkMode) CalculatorInfoDark else CalculatorSuccess
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedText("থিম", "Theme"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        if (isDarkMode) "ডার্ক মোড চালু আছে" else "লাইট মোড চালু আছে",
                        if (isDarkMode) "Dark mode is enabled" else "Light mode is enabled"
                    ),
                    fontSize = 12.sp,
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
            IosStyleSwitch(
                checked = isDarkMode,
                onCheckedChange = onThemeChange
            )
        }
    }
}

@Composable
private fun IosStyleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackColor = if (checked) CalculatorSuccess else Color(0xFFE5E7EB)
    val thumbOffset = if (checked) 26.dp else 2.dp

    Surface(
        modifier = Modifier
            .size(width = 56.dp, height = 32.dp)
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(999.dp),
        color = trackColor,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(28.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp
            ) {}
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = HomeTextPrimary,
            fontFamily = TiroBanglaFontFamily,
            modifier = Modifier.padding(start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, HomeBorder)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun PreferenceChoiceRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (selected) HomeSoftGreen else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIcon(icon = icon, selected = selected)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextPrimary,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                color = CalculatorMuted,
                fontFamily = TiroBanglaFontFamily
            )
        }
        if (selected) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = CalculatorSuccess
            )
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, title: String, subtitle: String) {
    StaticRow(icon = icon, title = title, subtitle = subtitle, trailingIcon = null, onClick = null)
}

@Composable
private fun ActionRow(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    StaticRow(
        icon = icon,
        title = title,
        subtitle = subtitle,
        trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
        onClick = onClick
    )
}

@Composable
private fun StaticRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingIcon: ImageVector?,
    onClick: (() -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingIcon(icon = icon, selected = false)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextPrimary,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                subtitle,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = CalculatorFieldText,
                fontFamily = TiroBanglaFontFamily
            )
        }
        if (trailingIcon != null) {
            Icon(
                trailingIcon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = CalculatorMutedSoft
            )
        }
    }
}

@Composable
private fun SettingIcon(icon: ImageVector, selected: Boolean) {
    Surface(
        color = if (selected) CalculatorSuccess else HomeSoftBlue,
        shape = RoundedCornerShape(13.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.padding(9.dp).size(20.dp),
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else CalculatorInfoDark
        )
    }
}

private fun Context.getAppVersionName(): String {
    return runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0.6"
    }.getOrDefault("1.0.6")
}

private fun Context.shareApp() {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Tax Calculator BD ব্যবহার করে অফলাইনে আয়কর হিসাব ও TIN অডিট চেক করুন।"+
                "https://play.google.com/store/apps/details?id=$packageName".toUri())
    }
    startActivity(Intent.createChooser(shareIntent, "Tax Calculator BD অ্যাপটি শেয়ার করুন"))
}

private fun Context.openPlayStore() {
    val marketIntent = Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
    val webIntent = Intent(Intent.ACTION_VIEW,
        "https://play.google.com/store/apps/details?id=$packageName".toUri())
    try {
        startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(webIntent)
    }
}

private fun Context.sendFeedbackEmail() {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("contact.marufalam@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Tax Calculator BD feedback")
    }
    startActivity(Intent.createChooser(emailIntent, "ইমেইল পাঠান"))
}
