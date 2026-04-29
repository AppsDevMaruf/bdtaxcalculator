package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlueDark
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeNavInactive
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftNav
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftPurple
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable

@Composable
fun HomeScreen(
    onOpenTaxCalculator: () -> Unit,
    onOpenAuditChecker: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
    selectedDestination: AppDestination
) {
    Scaffold(
        topBar = {HomeTopBar()},
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            WelcomeSection()

            HomeServiceCard(
                icon = Icons.Default.Assessment,
                iconBg = HomeActionBlue,
                title = "ট্যাক্স ক্যালকুলেটর",
                description = "আপনার বর্তমান আয়, বেতন কাঠামো এবং ছাড়ের তথ্য অনুযায়ী আয়কর হিসাব করুন।",
                buttonText = "এখন হিসাব করুন",
                buttonColor = HomeActionBlue,
                accentBlock = HomeSoftBlue,
                badge = "করবর্ষ ${TaxDefaults.taxYearLabel}",
                onClick = onOpenTaxCalculator
            )

            HomeServiceCard(
                icon = Icons.Default.Shield,
                iconBg = MaterialTheme.colorScheme.primary,
                title = "অডিট চেক",
                description = "আপনার TIN অডিটে আছে কি না তা সম্পূর্ণ অফলাইনে দ্রুত যাচাই করুন।",
                buttonText = "স্ট্যাটাস দেখুন",
                buttonColor = MaterialTheme.colorScheme.primary,
                accentBlock = HomeSoftGreen,
                badge = "অফলাইন ও প্রাইভেট",
                onClick = onOpenAuditChecker
            )

            FilingStatusCard()
        }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()+8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Surface(
                color = HomeSoftBlue,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "প্রোফাইল",
                    tint = HomeTextPrimary,
                    modifier = Modifier.padding(8.dp).size(28.dp)
                )
            }
            Text(
                "ট্যাক্সপ্রো",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HomeActionBlueDark,
                fontFamily = TiroBanglaFontFamily
            )
        }

        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = "নোটিফিকেশন",
            tint = HomeTextMuted,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun WelcomeSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "স্বাগতম!",
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = HomeActionBlue,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            "২০২৫-২৬ করবর্ষের জন্য আপনার ট্যাক্স সারসংক্ষেপ প্রস্তুত আছে।",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = HomeTextPrimary,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun HomeServiceCard(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    description: String,
    buttonText: String,
    buttonColor: Color,
    accentBlock: Color,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick)
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = iconBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            icon,
                            contentDescription = title,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(14.dp).size(20.dp)
                        )
                    }
                    Surface(
                        color = accentBlock,
                        shape = RoundedCornerShape(999.dp)
                    ) {
                        Text(
                            badge,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = HomeTextMuted,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        title,
                        fontSize = 24.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HomeActionBlue,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Text(
                        description,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = HomeTextPrimary,
                        fontFamily = TiroBanglaFontFamily
                    )
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        buttonText,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun FilingStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = HomeSoftPurple),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HomeInfoRow(
                icon = Icons.AutoMirrored.Filled.ShowChart,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBg = HomeSoftGreen,
                label = "ফাইলিং স্ট্যাটাস",
                value = "চলমান"
            )

            HomeInfoRow(
                icon = Icons.Default.CalendarMonth,
                iconTint = HomeActionBlue,
                iconBg = HomeSoftBlue,
                label = "শেষ তারিখ",
                value = "১৫ অক্টোবর, ২০২৪"
            )

            Text(
                "বিস্তারিত দেখুন →",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun HomeInfoRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Surface(
            color = iconBg,
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.padding(12.dp).size(20.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = HomeTextMuted,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HomeActionBlue,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
fun HomeBottomNavigation(
    selectedDestination: AppDestination,
    onOpenHome: () -> Unit,
    onOpenTaxCalculator: () -> Unit,
    onOpenAuditChecker: () -> Unit,
    onOpenProfile: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "হোম",
                isSelected = selectedDestination == AppDestination.Home,
                onClick = onOpenHome
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                label = "ক্যালকুলেটর",
                isSelected = selectedDestination == AppDestination.TaxCalculator,
                onClick = onOpenTaxCalculator
            )
            BottomNavItem(
                icon = Icons.Default.Security,
                label = "অডিট",
                isSelected = selectedDestination == AppDestination.AuditChecker,
                onClick = onOpenAuditChecker
            )
            BottomNavItem(
                icon = Icons.Default.PersonOutline,
                label = "প্রোফাইল",
                isSelected = selectedDestination == AppDestination.Profile,
                onClick = onOpenProfile
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.noRippleClickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            color = if (isSelected) HomeSoftNav else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = if (isSelected) HomeActionBlue else HomeNavInactive,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) HomeActionBlue else HomeNavInactive,
            fontFamily = TiroBanglaFontFamily
        )
    }
}
