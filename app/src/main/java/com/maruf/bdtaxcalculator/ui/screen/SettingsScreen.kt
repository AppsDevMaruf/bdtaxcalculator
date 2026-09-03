package com.maruf.bdtaxcalculator.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.tax.LocalTaxPreferenceStore
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.TaxpayerType
import com.maruf.bdtaxcalculator.tax.TaxYearCatalog
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.AppUiPreferences
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientBottom
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientTop
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import androidx.core.net.toUri
import java.util.Locale

@Composable
private fun TaxpayerType.localizedLabel(): String {
    return when (id) {
        "general" -> localizedText("সাধারণ করদাতা", "General taxpayer")
        "women" -> localizedText("মহিলা করদাতা", "Female taxpayer")
        "senior" -> localizedText("সিনিয়র সিটিজেন (৬৫+)", "Senior citizen (65+)")
        "thirdGender" -> localizedText("তৃতীয় লিঙ্গ", "Third gender")
        "disabled" -> localizedText("প্রতিবন্ধী", "Person with disability")
        "freedomFighter" -> localizedText(
            "যুদ্ধাহত মুক্তিযোদ্ধা / আহত জুলাই যোদ্ধা",
            "War-wounded freedom fighter / injured July fighter"
        )
        else -> label
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    language: String,
    themeMode: String,
    onLanguageChange: (String) -> Unit,
    onThemeModeChange: (String) -> Unit,
    themePalette: String,
    onThemePaletteChange: (String) -> Unit,
    onRateApp: () -> Unit
) {
    val context = LocalContext.current
    var selectedTaxpayerType by remember {
        mutableStateOf(LocalTaxPreferenceStore.getDefaultTaxpayerType(context))
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
            .background(CalculatorBackground)
            .background(
                Brush.verticalGradient(
                    listOf(
                        CalculatorGradientTop,
                        CalculatorGradientMiddle,
                        CalculatorGradientBottom
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        SettingsHeader()
        Spacer(modifier = Modifier.size(20.dp))

        AppLanguageRowCard(
            language = language,
            onClick = { showLanguageSheet = true }
        )

        Spacer(modifier = Modifier.size(18.dp))

        val isDarkMode = themeMode == AppUiPreferences.themeDark
        ThemeToggleCard(
            isDarkMode = isDarkMode,
            onThemeChange = { isDark ->
                onThemeModeChange(
                    if (isDark) AppUiPreferences.themeDark else AppUiPreferences.themeLight
                )
            }
        )

        AnimatedVisibility(
            visible = isDarkMode,
            enter = fadeIn(animationSpec = tween(180)) +
                expandVertically(
                    animationSpec = tween(260),
                    expandFrom = Alignment.Top
                ),
            exit = fadeOut(animationSpec = tween(140)) +
                shrinkVertically(
                    animationSpec = tween(220),
                    shrinkTowards = Alignment.Top
                )
        ) {
            Column(modifier = Modifier.animateContentSize(animationSpec = tween(260))) {
                Spacer(modifier = Modifier.size(18.dp))

                ThemePaletteCard(
                    themePalette = themePalette,
                    onThemePaletteChange = onThemePaletteChange
                )

                Spacer(modifier = Modifier.size(18.dp))
            }
        }

        if (!isDarkMode) {
            Spacer(modifier = Modifier.size(18.dp))
        }

        SettingsSection(title = localizedText("লোকাল ট্যাক্স সেটআপ", "Local tax setup")) {
            Text(
                localizedText(
                    "নতুন হিসাব শুরু হলে করদাতার শ্রেণী ডিফল্ট হিসেবে ব্যবহার হবে।",
                    "This taxpayer category is used as the default for new calculations."
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                color = CalculatorMuted,
                fontFamily = TiroBanglaFontFamily
            )

            TaxDefaults.taxpayerTypes.forEach { type ->
                PreferenceChoiceRow(
                    icon = type.icon ?: Icons.Default.Person,
                    title = type.localizedLabel(),
                    subtitle = localizedText(
                        "করমুক্ত সীমা: ৳ ${formatBengaliNumber(type.taxFreeLimit)}",
                        "Tax-free limit: BDT ${formatBengaliNumber(type.taxFreeLimit)}"
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

        SettingsSection(title = localizedText("প্রাইভেসি ও ডেটা", "Privacy & data")) {
            InfoRow(
                icon = Icons.Default.Lock,
                title = localizedText("লোকাল ও প্রাইভেট", "Local and private"),
                subtitle = localizedText(
                    "কোনো লগইন, নিজস্ব সার্ভার, ডেটাবেস বা ইনপুট সংরক্ষণ নেই।",
                    "No login, own server, database, or saved tax input."
                )
            )
            InfoRow(
                icon = Icons.Default.Language,
                title = localizedText("NBR ওয়েবসাইট লিংক", "NBR website link"),
                subtitle = localizedText(
                    "TIN যাচাইয়ের জন্য NBR ওয়েবসাইট খোলা হয়; অ্যাপ তথ্য সংরক্ষণ করে না।",
                    "TIN check opens the NBR website; the app does not store submitted data."
                )
            )
            InfoRow(
                icon = Icons.Default.Info,
                title = localizedText("রুলস ডেটা", "Rules data"),
                subtitle = localizedText(
                    "আয়বর্ষ ${TaxDefaults.incomeYearLabel}; অ্যাসেসমেন্ট ইয়ার ${TaxDefaults.assessmentYearLabel}; অডিট ডেটা অ্যাপের static JSON থেকে লোড হয়।",
                    "Income year 2025-26; assessment year 2026-27; audit data loads from static JSON inside the app."
                )
            )
            InfoRow(
                icon = Icons.Default.Settings,
                title = localizedText("শুধু লোকাল পছন্দ", "Local preferences only"),
                subtitle = localizedText(
                    "ডিফল্ট করদাতা ও calculator screen-এর tax rule পছন্দ শুধু এই ডিভাইসে থাকে।",
                    "Default taxpayer and calculator tax-rule choices stay only on this device."
                )
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = localizedText("তথ্যের উৎস", "Data sources")) {
            Text(
                localizedText(
                    "BD Tax Calculator একটি স্বাধীন অ্যাপ। এটি National Board of Revenue (NBR) বা কোনো সরকারি প্রতিষ্ঠানের সাথে সংযুক্ত, অনুমোদিত বা প্রতিনিধিত্বকারী নয়। নিচের লিংকগুলো official NBR উৎস।",
                    "BD Tax Calculator is an independent app. It is not affiliated with, endorsed by, or authorized by the National Board of Revenue (NBR) or any government entity. The links below open official NBR sources."
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 4.dp),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                color = CalculatorMuted,
                fontFamily = TiroBanglaFontFamily
            )

            TaxYearCatalog.supportedYears.forEach { rules ->
                ActionRow(
                    icon = Icons.Default.PictureAsPdf,
                    title = localizedText(
                        "আয়কর পরিপত্র ${rules.incomeYear}",
                        "Income Tax Circular ${rules.incomeYear}"
                    ),
                    subtitle = localizedText(
                        "আয়বর্ষ ${rules.incomeYear} • করবর্ষ ${rules.assessmentYear} • NBR PDF",
                        "Income year ${rules.incomeYear} • Assessment year ${rules.assessmentYear} • NBR PDF"
                    ),
                    onClick = {
                        FirebaseTracker.logSettingsAction("open_nbr_circular_${rules.incomeYear}")
                        context.openExternalUrl(rules.officialSourceUrl)
                    }
                )
            }

            ActionRow(
                icon = Icons.Default.Language,
                title = localizedText("সব আয়কর পরিপত্র", "All income-tax circulars"),
                subtitle = localizedText(
                    "NBR ওয়েবসাইটের আয়কর পরিপত্র তালিকা খুলুন।",
                    "Open the income-tax circular index on the NBR website."
                ),
                onClick = {
                    FirebaseTracker.logSettingsAction("open_nbr_circular_index")
                    context.openExternalUrl(NbrIncomeTaxCircularIndexUrl)
                }
            )

            Text(
                localizedText(
                    "নোট: কর তথ্য পাবলিক NBR উৎসের ভিত্তিতে আনুমানিক হিসাবের সহায়তার জন্য। রিটার্ন দাখিল বা চূড়ান্ত সিদ্ধান্তের আগে মূল পরিপত্র এবং প্রযোজ্য আইন যাচাই করুন।",
                    "Note: Tax information is based on public NBR sources and provided for estimation help. Check the original circular and applicable law before filing a return or making a final decision."
                ),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
                color = CalculatorFieldText,
                fontFamily = TiroBanglaFontFamily
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
                onClick = {
                    FirebaseTracker.logSettingsAction("share_app")
                    context.shareApp()
                }
            )
            ActionRow(
                icon = Icons.Default.Language,
                title = localizedText("ফেসবুক পেজ", "Facebook Page"),
                subtitle = localizedText(
                    "BD Tax Calculator পেজ ফলো করুন।",
                    "Follow the BD Tax Calculator page."
                ),
                onClick = {
                    FirebaseTracker.logSettingsAction("open_facebook_page")
                    context.openExternalUrl(FacebookPageUrl)
                }
            )
            ActionRow(
                icon = Icons.Default.Person,
                title = localizedText("ইউজার কমিউনিটি", "User Community"),
                subtitle = localizedText(
                    "BD Tax Calculator User Community গ্রুপে যুক্ত হন।",
                    "Join the BD Tax Calculator User Community group."
                ),
                onClick = {
                    FirebaseTracker.logSettingsAction("open_facebook_group")
                    context.openExternalUrl(FacebookGroupUrl)
                }
            )
            ActionRow(
                icon = Icons.Default.Star,
                title = localizedText("রেট দিন", "Rate app"),
                subtitle = localizedText("Play Store-এ রেটিং দিন।", "Rate us on the Play Store."),
                onClick = {
                    FirebaseTracker.logSettingsAction("rate_app")
                    onRateApp()
                }
            )
            ActionRow(
                icon = Icons.Default.Email,
                title = localizedText("সমস্যা জানান", "Report issue"),
                subtitle = localizedText(
                    "ভুল হিসাব বা ডেটা সমস্যা জানাতে ইমেইল করুন।",
                    "Email us about calculation or data issues."
                ),
                onClick = {
                    FirebaseTracker.logSettingsAction("report_issue")
                    context.sendFeedbackEmail()
                }
            )
            ActionRow(
                icon = Icons.Default.Delete,
                title = localizedText("লোকাল সেটিংস রিসেট", "Reset local settings"),
                subtitle = localizedText(
                    "ডিফল্ট করদাতা ও calculator tax rule পছন্দ পরিষ্কার করুন।",
                    "Clear default taxpayer and calculator tax-rule choices."
                ),
                onClick = {
                    FirebaseTracker.logSettingsAction("reset_local_settings")
                    LocalTaxPreferenceStore.clear(context)
                    selectedTaxpayerType = LocalTaxPreferenceStore.getDefaultTaxpayerType(context)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
            localizedText(
                "স্বাধীন অ্যাপ • Official source: nbr.gov.bd",
                "Independent app • Official source: nbr.gov.bd"
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontWeight = FontWeight.Normal,
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

        Spacer(modifier = Modifier.size(FloatingBottomBarSafePadding))
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
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
        shape = RoundedCornerShape(10.dp),
        //color = HomeSoftGreen,
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
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(color = HomeSoftBlue, shape = RoundedCornerShape(12.dp)) {
                Text(
                    if (language == AppUiPreferences.languageEnglish) "\uD83C\uDDFA\uD83C\uDDF8" else "\uD83C\uDDE7\uD83C\uDDE9",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                    fontSize = 16.sp
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedText("অ্যাপের ভাষা", "App Language"),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    if (language == AppUiPreferences.languageEnglish) "English" else "বাংলা",
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
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
        containerColor = CalculatorPanel,
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        "ভাষা পুরো অ্যাপে প্রয়োগ হবে",
                        "Language changes will be applied to the entire app"
                    ),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
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
                title = localizedText("বাংলা", "Bangla"),
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
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) HomeSoftGreen else Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
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
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = CalculatorSuccess,
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
    val iconContainerColor by animateColorAsState(
        targetValue = if (isDarkMode) HomeSoftGreen else CalculatorSurfaceAlt,
        animationSpec = tween(220),
        label = "theme_icon_container_color"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isDarkMode) CalculatorSuccess else CalculatorMutedSoft,
        animationSpec = tween(220),
        label = "theme_icon_tint"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(260)),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = iconContainerColor,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    if (isDarkMode) Icons.Default.DarkMode else Icons.Default.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(12.dp),
                    tint = iconTint
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    localizedText("থিম", "Theme"),
                    fontSize = 12.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    localizedText(
                        if (isDarkMode) "ডার্ক মোড চালু আছে" else "লাইট মোড চালু আছে",
                        if (isDarkMode) "Dark mode is enabled" else "Light mode is enabled"
                    ),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
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
private fun ThemePaletteCard(
    themePalette: String,
    onThemePaletteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    color = CalculatorSurfaceAlt,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp).size(22.dp),
                        tint = CalculatorSuccess
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        localizedText("ডার্ক থিম কালার", "Choose the Dark Theme color"),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HomeTextPrimary,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Text(
                        localizedText(
                            "আপনার পছন্দের কালার বেছে নিন",
                            "Choose your preferred app color style"
                        ),
                        fontSize = 12.sp,
                        color = CalculatorMuted,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }

            PaletteOptionRow(
                title = localizedText("অলিভ নাইট", "Olive Night"),
                subtitle = localizedText(
                    "Wise-style গভীর অলিভ ডার্ক প্যাটার্ন",
                    "Wise-style deep olive dark pattern"
                ),
                selected = themePalette == AppUiPreferences.themePaletteOlive,
                swatches = listOf(Color(0xFF163300)),
                onClick = { onThemePaletteChange(AppUiPreferences.themePaletteOlive) }
            )

            PaletteOptionRow(
                title = localizedText("ক্লাসিক গ্রিন", "Classic Green"),
                subtitle = localizedText(
                    "আগের এমেরাল্ড/গ্রিন ডার্ক প্যাটার্ন",
                    "Previous emerald green dark pattern"
                ),
                selected = themePalette == AppUiPreferences.themePaletteClassicGreen,
                swatches = listOf(Color(0xFF001E16)),
                onClick = { onThemePaletteChange(AppUiPreferences.themePaletteClassicGreen) }
            )

            PaletteOptionRow(
                title = localizedText("মিডনাইট ব্ল্যাক", "Midnight Black"),
                subtitle = localizedText(
                    "কালো ব্যাকগ্রাউন্ডের মিনিমাল ডার্ক প্যাটার্ন",
                    "Minimal dark pattern based on black"
                ),
                selected = themePalette == AppUiPreferences.themePaletteMidnightBlack,
                swatches = listOf(Color(0xFF000000)),
                onClick = { onThemePaletteChange(AppUiPreferences.themePaletteMidnightBlack) }
            )
        }
    }
}

@Composable
private fun PaletteOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    swatches: List<Color>,
    onClick: () -> Unit
) {
    val rowBackground by animateColorAsState(
        targetValue = if (selected) HomeSoftGreen else Color.Transparent,
        animationSpec = tween(180),
        label = "palette_option_background"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(180))
            .clickable(onClick = onClick)
            .background(rowBackground)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            swatches.forEach { color ->
                Surface(
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = color,
                    border = BorderStroke(1.dp, HomeBorder)
                ) {}
            }
        }
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
        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(animationSpec = tween(140)),
            exit = fadeOut(animationSpec = tween(100))
        ) {
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
private fun IosStyleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackColor by animateColorAsState(
        targetValue = if (checked) CalculatorSuccess else HomeBorder,
        animationSpec = tween(220),
        label = "theme_switch_track_color"
    )
    val thumbColor by animateColorAsState(
        targetValue = CalculatorSurfaceAlt,
        animationSpec = tween(220),
        label = "theme_switch_thumb_color"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 26.dp else 2.dp,
        animationSpec = tween(220),
        label = "theme_switch_thumb_offset"
    )

    Surface(
        modifier = Modifier
            .size(width = 52.dp, height = 28.dp)
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
                    .size(24.dp),
                shape = CircleShape,
                color = thumbColor,
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
            colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(10.dp),
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingIcon(icon = icon, selected = selected)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center ) {
            Text(
                title,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextPrimary,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingIcon(icon = icon, selected = false)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = HomeTextPrimary,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                subtitle,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Normal,
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
        color = if (selected) CalculatorSuccess else CalculatorSurfaceAlt,
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.padding(6.dp).size(16.dp),
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else CalculatorMutedSoft
        )
    }
}

private fun Context.getAppVersionName(): String {
    return runCatching {
        packageManager.getPackageInfo(packageName, 0).versionName ?: "1.1.20"
    }.getOrDefault("1.1.20")
}

private const val FacebookPageUrl =
    "https://www.facebook.com/bdtaxcalculator"

private const val FacebookGroupUrl =
    "https://www.facebook.com/groups/1682838446173061"

private const val NbrIncomeTaxCircularIndexUrl =
    "https://nbr.gov.bd/taxtypes/income-tax/income-tax-paripatra/ban"

private fun Context.openExternalUrl(url: String) {
    runCatching {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }.onFailure(FirebaseTracker::recordNonFatal)
}

private fun Context.shareApp() {
    val shareText = if (Locale.getDefault().language == "bn") {
        "Tax Calculator BD ব্যবহার করে অফলাইনে আয়কর হিসাব ও TIN অডিট চেক করুন।"
    } else {
        "Use Tax Calculator BD to calculate income tax and check TIN audit status offline."
    }
    val chooserTitle = if (Locale.getDefault().language == "bn") {
        "Tax Calculator BD অ্যাপটি শেয়ার করুন"
    } else {
        "Share Tax Calculator BD"
    }
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$shareText https://play.google.com/store/apps/details?id=$packageName")
    }
    startActivity(Intent.createChooser(shareIntent, chooserTitle))
}

private fun Context.sendFeedbackEmail() {
    val chooserTitle = if (Locale.getDefault().language == "bn") "ইমেইল পাঠান" else "Send email"
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("contact.marufalam@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Tax Calculator BD feedback")
    }
    startActivity(Intent.createChooser(emailIntent, chooserTitle))
}
