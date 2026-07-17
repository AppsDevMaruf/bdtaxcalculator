package com.maruf.bdtaxcalculator.ui.screen

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.notification.AppNotificationItem
import com.maruf.bdtaxcalculator.notification.AppNotificationStore
import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlueDark
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeNavInactive
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenTaxCalculator: () -> Unit,
    onOpenAuditChecker: () -> Unit,
    onOpenNbrTinCheck: () -> Unit,
    onOpenTaxFaq: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
    selectedDestination: AppDestination
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val notifications by AppNotificationStore.observe(context).collectAsState()
    var isNotificationSheetVisible by rememberSaveable { mutableStateOf(false) }
    val notificationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun closeNotificationSheet() {
        AppNotificationStore.markAllRead(context)
        isNotificationSheetVisible = false
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                unreadCount = notifications.count { !it.isRead },
                onNotificationsClick = {
                    FirebaseTracker.logNotificationInboxOpened(
                        totalCount = notifications.size,
                        unreadCount = notifications.count { !it.isRead }
                    )
                    AppNotificationStore.markAllRead(context)
                    isNotificationSheetVisible = true
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = 18.dp,
                        end = 16.dp,
                        bottom = FloatingBottomBarSafePadding
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            WelcomeSection()

            HomeServiceCard(
                icon = Icons.Default.Assessment,
                iconBg = HomeActionBlue,
                title = localizedText("ট্যাক্স ক্যালকুলেটর", "Tax Calculator"),
                description = localizedText(
                    "আপনার বর্তমান আয়, বেতন কাঠামো এবং ছাড়ের তথ্য অনুযায়ী আয়কর হিসাব করুন।",
                    "Estimate income tax from your salary, bonus, exemptions, and investment rebate."
                ),
                buttonText = localizedText("এখন হিসাব করুন", "Calculate Now"),
                buttonColor = HomeActionBlue,
                accentBlock = HomeSoftGreen,
                badge = localizedText(
                    "আয়বর্ষ ${TaxDefaults.incomeYearLabel} · অ্যাসেসমেন্ট ${TaxDefaults.assessmentYearLabel}",
                    "Income Year 2025-26 · AY 2026-27"
                ),
                onClick = {
                    FirebaseTracker.logHomeServiceOpened("tax_calculator")
                    onOpenTaxCalculator()
                }
            )

            HomeServiceCard(
                icon = Icons.Default.Security,
                iconBg = MaterialTheme.colorScheme.primary,
                title = localizedText("অডিট চেক", "Audit Check"),
                description = localizedText(
                    "আপনার TIN অডিটে আছে কি না তা সম্পূর্ণ অফলাইনে দ্রুত যাচাই করুন।",
                    "Check whether your TIN is in the NBR audit list, fully offline."
                ),
                buttonText = localizedText("স্ট্যাটাস দেখুন", "Check Status"),
                buttonColor = MaterialTheme.colorScheme.primary,
                accentBlock = HomeSoftGreen,
                badge = localizedText("অফলাইন ও প্রাইভেট", "Offline and Private"),
                onClick = {
                    FirebaseTracker.logHomeServiceOpened("audit_checker")
                    onOpenAuditChecker()
                }
            )

            HomeServiceCard(
                icon = Icons.Default.QuestionMark,
                iconBg = MaterialTheme.colorScheme.primary,
                title = localizedText("আয়কর সংক্রান্ত সচরাচর জিজ্ঞাসা", "Tax FAQ"),
                description = localizedText(
                    "NBR FAQ-এর আয়কর প্রশ্ন, রিটার্ন, e-TIN, e-Return ও রেয়াতের উত্তর এক জায়গায় দেখুন।",
                    "Browse NBR-style answers about income tax, returns, e-TIN, e-Return, and rebate in one place."
                ),
                buttonText = localizedText("FAQ দেখুন", "Browse FAQ"),
                buttonColor = MaterialTheme.colorScheme.primary,
                accentBlock = HomeSoftGreen,
                badge = localizedText("৮১টি প্রশ্ন", "81 questions"),
                onClick = {
                    FirebaseTracker.logHomeServiceOpened("tax_faq")
                    onOpenTaxFaq()
                }
            )

            ImportantGovtLinksCard(
                onOpenLink = { link ->
                    FirebaseTracker.logEvent(
                        "important_govt_link_opened",
                        Bundle().apply { putString("link", link.analyticsName) }
                    )
                    uriHandler.openUri(link.url)
                }
            )

            /*HomeServiceCard(
                icon = Icons.Default.AccountBalance,
                iconBg = CalculatorSuccess,
                title = localizedText("NBR TIN যাচাই", "NBR TIN Check"),
                description = localizedText(
                    "সরকারি NBR-Sonali Bank TAX portal খুলে TIN তথ্য যাচাই করুন।",
                    "Open the official NBR-Sonali Bank TAX portal to verify TIN details."
                ),
                buttonText = localizedText("সরকারি পোর্টাল খুলুন", "Open Official Portal"),
                buttonColor = CalculatorSuccess,
                accentBlock = HomeSoftGreen,
                badge = localizedText("ইন্টারনেট প্রয়োজন", "Internet Required"),
                onClick = {
                    FirebaseTracker.logHomeServiceOpened("nbr_tin_check")
                    onOpenNbrTinCheck()
                }
            )*/

            //FilingStatusCard()
        }
    }

    if (isNotificationSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = ::closeNotificationSheet,
            sheetState = notificationSheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(top = 10.dp),
                    color = HomeBorder,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Spacer(modifier = Modifier.size(width = 42.dp, height = 4.dp))
                }
            }
        ) {
            NotificationInboxSheet(
                notifications = notifications,
                onClear = {
                    FirebaseTracker.logNotificationInboxCleared(notifications.size)
                    AppNotificationStore.clear(context)
                }
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    unreadCount: Int,
    onNotificationsClick: () -> Unit
) {
    val bellBackground by animateColorAsState(
        targetValue = if (unreadCount > 0) HomeSoftGreen else MaterialTheme.colorScheme.surface,
        label = "notificationBellBackground"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()+8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                "Tax Calculator Bd",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HomeActionBlueDark,
                fontFamily = TiroBanglaFontFamily
            )
        }
        Box {
            Surface(
                modifier = Modifier.noRippleClickable(onClick = onNotificationsClick),
                color = bellBackground,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, HomeBorder)
            ) {
                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.NotificationsNone,
                        contentDescription = localizedText("নোটিফিকেশন", "Notifications"),
                        tint = if (unreadCount > 0) CalculatorSuccess else HomeTextMuted,
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
            NotificationBadge(unreadCount = unreadCount)
        }
    }
}

@Composable
private fun BoxScope.NotificationBadge(unreadCount: Int) {
    AnimatedVisibility(
        visible = unreadCount > 0,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 5.dp, y = (-6).dp)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.error,
            shape = CircleShape,
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notificationBadgeText(unreadCount),
                    color = MaterialTheme.colorScheme.onError,
                    fontSize = 10.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun notificationBadgeText(unreadCount: Int): String {
    val englishText = if (unreadCount > 9) "9+" else unreadCount.toString()
    return localizedText(englishText.toBanglaDigits(), englishText)
}

@Composable
private fun NotificationInboxSheet(
    notifications: List<AppNotificationItem>,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = localizedText("নোটিফিকেশন", "Notifications"),
                    color = HomeTextPrimary,
                    fontSize = 24.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )

            AnimatedVisibility(
                visible = notifications.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                TextButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = localizedText("পরিষ্কার", "Clear"),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }
        }

        HorizontalDivider(color = CalculatorBorder)

        if (notifications.isEmpty()) {
            EmptyNotificationState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationInboxItem(item = item)
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        border = BorderStroke(1.dp, CalculatorBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = CalculatorAccentSoft,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = CalculatorSuccess,
                    modifier = Modifier.padding(14.dp).size(24.dp)
                )
            }
            Text(
                text = localizedText("এখনো কোনো নোটিফিকেশন নেই", "No notifications yet"),
                color = HomeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText(
                    "নোটিফিকেশন এলে এই inbox-এ জমা হবে।",
                    "New notifications will appear in this inbox."
                ),
                color = CalculatorMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun NotificationInboxItem(item: AppNotificationItem) {
    val backgroundColor by animateColorAsState(
        targetValue = if (item.isRead) CalculatorPanel else CalculatorAccentSoft,
        label = "notificationItemBackground"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, if (item.isRead) CalculatorBorder else CalculatorSuccess),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (item.isRead) CalculatorAccentSoft else CalculatorSuccess,
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = if (item.isRead) CalculatorSuccess else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(10.dp).size(18.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.title,
                    color = HomeTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = item.body,
                    color = CalculatorMuted,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = item.formattedTime(),
                    color = CalculatorMutedSoft,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

private fun AppNotificationItem.formattedTime(): String {
    return SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        .format(Date(receivedAtMillis))
}

private fun String.toBanglaDigits(): String {
    return map { char ->
        when (char) {
            '0' -> '০'
            '1' -> '১'
            '2' -> '২'
            '3' -> '৩'
            '4' -> '৪'
            '5' -> '৫'
            '6' -> '৬'
            '7' -> '৭'
            '8' -> '৮'
            '9' -> '৯'
            else -> char
        }
    }.joinToString("")
}

@Composable
private fun WelcomeSection() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            localizedText("স্বাগতম!", "Welcome back!"),
            fontSize = 28.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = HomeActionBlue,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            localizedText(
                "আয়বর্ষ ${TaxDefaults.incomeYearLabel} ও অ্যাসেসমেন্ট ${TaxDefaults.assessmentYearLabel}-এর জন্য আপনার ট্যাক্স সারসংক্ষেপ প্রস্তুত আছে।",
                "Your tax overview for income year 2025-26 and assessment year 2026-27 is ready."
            ),
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
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .noRippleClickable(onClick = onClick)
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

private data class GovtLink(
    val banglaTitle: String,
    val englishTitle: String,
    val url: String,
    val analyticsName: String
)

private val importantGovtLinks = listOf(
    GovtLink("e-TDS", "e-TDS", "https://etds.gov.bd", "e_tds"),
    GovtLink("e-TIN", "e-TIN", "https://secure.incometax.gov.bd/TINHome", "e_tin"),
    GovtLink("a-Challan", "a-Challan", "https://ibas.finance.gov.bd", "a_challan"),
    GovtLink("NBR Website", "NBR Website", "https://nbr.gov.bd", "nbr_website"),
    GovtLink("Sonali Bank Payment", "Sonali Bank Payment", "https://nbr.sblesheba.com/IncomeTax/Payment", "sonali_bank_payment"),
    GovtLink("e-Return", "e-Return", "https://etaxnbr.gov.bd", "e_return"),
    GovtLink("Return Verify", "Return Verify", "https://etaxnbr.gov.bd", "return_verify")
)

@Composable
private fun ImportantGovtLinksCard(
    onOpenLink: (GovtLink) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionTitle(
                title = localizedText("গুরুত্বপূর্ণ সরকারি লিংক", "Important Govt. Links"),
                subtitle = localizedText(
                    "Official NBR ও related service দ্রুত খুলুন।",
                    "Quickly open official NBR and related services."
                )
            )

            importantGovtLinks.chunked(2).forEach { rowLinks ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowLinks.forEach { link ->
                        GovtLinkChip(
                            modifier = Modifier.weight(1f),
                            text = localizedText(link.banglaTitle, link.englishTitle),
                            onClick = { onOpenLink(link) }
                        )
                    }
                    if (rowLinks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun GovtLinkChip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.noRippleClickable(onClick = onClick),
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 11.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 12.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            title,
            color = HomeTextPrimary,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            subtitle,
            color = HomeTextMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun FilingStatusCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, HomeBorder),
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
                label = localizedText("ফাইলিং স্ট্যাটাস", "Filing Status"),
                value = localizedText("চলমান", "In Progress")
            )

            HomeInfoRow(
                icon = Icons.Default.CalendarMonth,
                iconTint = HomeActionBlue,
                iconBg = HomeSoftBlue,
                label = localizedText("শেষ তারিখ", "Deadline"),
                value = localizedText("১৫ অক্টোবর, ২০২৪", "Oct 15, 2024")
            )

            Text(
                localizedText("বিস্তারিত দেখুন →", "View Details →"),
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
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
            shape = RoundedCornerShape(32.dp),
            tonalElevation = 0.dp,
            shadowElevation = 14.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Default.Home,
                    label = localizedText("হোম", "Home"),
                    isSelected = selectedDestination == AppDestination.Home,
                    onClick = onOpenHome
                )
                BottomNavItem(
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    label = localizedText("ক্যালকুলেটর", "Calculator"),
                    isSelected = selectedDestination == AppDestination.TaxCalculator,
                    onClick = onOpenTaxCalculator
                )
                BottomNavItem(
                    icon = Icons.Default.Security,
                    label = localizedText("অডিট", "Audit"),
                    isSelected = selectedDestination == AppDestination.AuditChecker,
                    onClick = onOpenAuditChecker
                )
                BottomNavItem(
                    icon = Icons.Default.Settings,
                    label = localizedText("সেটিংস", "Settings"),
                    isSelected = selectedDestination == AppDestination.Profile,
                    onClick = onOpenProfile
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val itemWidth by animateDpAsState(
        targetValue = if (isSelected) 106.dp else 48.dp,
        animationSpec = tween(durationMillis = 260),
        label = "bottomNavWidth"
    )
    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.94f,
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavScale"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            CalculatorSurfaceAlt
        },
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            HomeNavInactive
        },
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavContent"
    )

    Surface(
        modifier = Modifier
            .width(itemWidth)
            .height(48.dp)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
            }
            .noRippleClickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                CalculatorBorder
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (isSelected) 12.dp else 14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally(
                    animationSpec = tween(durationMillis = 240),
                    expandFrom = Alignment.Start
                ) + fadeIn(animationSpec = tween(durationMillis = 180)),
                exit = shrinkHorizontally(
                    animationSpec = tween(durationMillis = 180),
                    shrinkTowards = Alignment.Start
                ) + fadeOut(animationSpec = tween(durationMillis = 120))
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 7.dp),
                    maxLines = 1,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}
