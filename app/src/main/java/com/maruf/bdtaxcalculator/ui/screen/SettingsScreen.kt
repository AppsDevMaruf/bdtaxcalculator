package com.maruf.bdtaxcalculator.ui.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.tax.LocalTaxPreferenceStore
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.formatBengaliNumber
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBackground
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInfoDark
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import androidx.core.net.toUri

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var selectedTaxpayerType by remember {
        mutableStateOf(LocalTaxPreferenceStore.getDefaultTaxpayerType(context))
    }
    var selectedAssessmentType by remember {
        mutableStateOf(LocalTaxPreferenceStore.getAssessmentType(context))
    }
    val appVersion = remember(context.packageName) { context.getAppVersionName() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalculatorBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {
        SettingsHeader()

        Spacer(modifier = Modifier.size(20.dp))

        PrivacyHeroCard()

        Spacer(modifier = Modifier.size(20.dp))

        SettingsSection(title = "লোকাল ট্যাক্স সেটআপ") {
            Text(
                "নতুন হিসাব শুরু হলে এই সেটিংস ডিফল্ট হিসেবে ব্যবহার হবে।",
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
                    subtitle = "করমুক্ত সীমা: ৳ ${formatBengaliNumber(type.taxFreeLimit)}",
                    selected = selectedTaxpayerType == type.id,
                    onClick = {
                        selectedTaxpayerType = type.id
                        LocalTaxPreferenceStore.setDefaultTaxpayerType(context, type.id)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = "অ্যাসেসমেন্ট ধরন") {
            PreferenceChoiceRow(
                icon = Icons.Default.CheckCircle,
                title = "সাধারণ অ্যাসেসমেন্ট",
                subtitle = "ন্যূনতম কর: ৳ ${formatBengaliNumber(TaxDefaults.minimumTax.toLong())}",
                selected = selectedAssessmentType == LocalTaxPreferenceStore.assessmentRegular,
                onClick = {
                    selectedAssessmentType = LocalTaxPreferenceStore.assessmentRegular
                    LocalTaxPreferenceStore.setAssessmentType(context, LocalTaxPreferenceStore.assessmentRegular)
                }
            )
            PreferenceChoiceRow(
                icon = Icons.Default.Calculate,
                title = "নতুন অ্যাসেসমেন্ট",
                subtitle = "ন্যূনতম কর: ৳ ${formatBengaliNumber(TaxDefaults.newAssessmentMinimumTax.toLong())}",
                selected = selectedAssessmentType == LocalTaxPreferenceStore.assessmentNew,
                onClick = {
                    selectedAssessmentType = LocalTaxPreferenceStore.assessmentNew
                    LocalTaxPreferenceStore.setAssessmentType(context, LocalTaxPreferenceStore.assessmentNew)
                }
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = "প্রাইভেসি ও ডেটা") {
            InfoRow(
                icon = Icons.Default.Lock,
                title = "অফলাইন ও প্রাইভেট",
                subtitle = "কোনো লগইন, সার্ভার, ডেটাবেস বা ইনপুট সংরক্ষণ নেই।"
            )
            InfoRow(
                icon = Icons.Default.Info,
                title = "রুলস ডেটা",
                subtitle = "করবর্ষ ${TaxDefaults.taxYearLabel}; অডিট ডেটা অ্যাপের static JSON থেকে লোড হয়।"
            )
            InfoRow(
                icon = Icons.Default.Settings,
                title = "শুধু লোকাল পছন্দ",
                subtitle = "ডিফল্ট করদাতা ও অ্যাসেসমেন্ট ধরন শুধু এই ডিভাইসে থাকে।"
            )
        }

        Spacer(modifier = Modifier.size(18.dp))

        SettingsSection(title = "সহায়তা") {
            ActionRow(
                icon = Icons.Default.Share,
                title = "অ্যাপ শেয়ার করুন",
                subtitle = "Tax Calculator BD অন্যদের জানাতে শেয়ার করুন।",
                onClick = { context.shareApp() }
            )
            ActionRow(
                icon = Icons.Default.Star,
                title = "রেট দিন",
                subtitle = "Play Store-এ রেটিং দিন।",
                onClick = { context.openPlayStore() }
            )
            ActionRow(
                icon = Icons.Default.Email,
                title = "সমস্যা জানান",
                subtitle = "ভুল হিসাব বা ডেটা সমস্যা জানাতে ইমেইল করুন।",
                onClick = { context.sendFeedbackEmail() }
            )
            ActionRow(
                icon = Icons.Default.Delete,
                title = "লোকাল সেটিংস রিসেট",
                subtitle = "ডিফল্ট করদাতা ও অ্যাসেসমেন্ট সেটিংস পরিষ্কার করুন।",
                onClick = {
                    LocalTaxPreferenceStore.clear(context)
                    selectedTaxpayerType = LocalTaxPreferenceStore.getDefaultTaxpayerType(context)
                    selectedAssessmentType = LocalTaxPreferenceStore.getAssessmentType(context)
                }
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
            "সংস্করণ $appVersion",
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
                    "সেটিংস",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeTextPrimary,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    "প্রাইভেসি-ফার্স্ট লোকাল কনফিগারেশন",
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
                        "আপনার ডেটা আপনার ডিভাইসে",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeTextPrimary,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Text(
                        "Tax Calculator BD কোনো ব্যক্তিগত প্রোফাইল তৈরি করে না।",
                        fontSize = 13.sp,
                        color = CalculatorMuted,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrivacyPill("নো লগইন")
                PrivacyPill("নো সার্ভার")
                PrivacyPill("নো ইনপুট সেভ")
            }
        }
    }
}

@Composable
private fun PrivacyPill(label: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
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
        packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0.5"
    }.getOrDefault("1.0.5")
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
