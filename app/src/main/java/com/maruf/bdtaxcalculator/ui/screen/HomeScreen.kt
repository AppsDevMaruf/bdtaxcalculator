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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.R
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.firebase.FirebaseHomeNews
import com.maruf.bdtaxcalculator.firebase.FirebaseHomeNewsStore
import com.maruf.bdtaxcalculator.notification.AppNotificationItem
import com.maruf.bdtaxcalculator.notification.AppNotificationStore
import com.maruf.bdtaxcalculator.ui.content.incomeTaxFaqs
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorHeroMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorHeroStart
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlueDark
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeNavInactive
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftPurple
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
    onOpenNotices: () -> Unit,
    onOpenLawyerBooking: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenProfile: () -> Unit,
    selectedDestination: AppDestination
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val notifications by AppNotificationStore.observe(context).collectAsState()
    val homeNews by FirebaseHomeNewsStore.observe().collectAsState()
    val faqCount = incomeTaxFaqs().size
    var isNotificationSheetVisible by rememberSaveable { mutableStateOf(false) }
    val notificationSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) {
        FirebaseHomeNewsStore.refresh()
    }

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
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            HomeSoftGreen,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                        bottom = FloatingBottomBarSafePadding
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (homeNews.isEnabled) {
                    FirebaseNewsTicker(news = homeNews)
                }

                TaxCalculatorHero(
                    onClick = {
                        FirebaseTracker.logHomeServiceOpened("tax_calculator")
                        onOpenTaxCalculator()
                    }
                )
                LawyerConsultationShortcut(
                    onClick = {
                        FirebaseTracker.logHomeServiceOpened("lawyer_booking")
                        onOpenLawyerBooking()
                    }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    icon = Icons.Default.Star,
                    title = localizedText("দ্রুত সেবা", "Quick actions"),
                    subtitle = localizedText(
                        "এক ট্যাপেই দরকারি আয়কর সেবা।",
                        "Useful tax tools, only one tap away."
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Shield,
                        iconResource = R.drawable.ic_audit_check,
                        title = localizedText("অডিট চেক", "Audit Check"),
                        description = localizedText(
                            "আপনার TIN অডিট তালিকায় আছে কি না সম্পূর্ণ অফলাইনে যাচাই করুন।",
                            "Check whether your TIN is in the NBR audit list, fully offline."
                        ),
                        badge = localizedText("অফলাইন · AY ২০২৩–২৪", "Offline · AY 2023–24"),
                        accent = HomeActionBlue,
                        accentBackground = HomeSoftBlue,
                        onClick = {
                            FirebaseTracker.logHomeServiceOpened("audit_checker")
                            onOpenAuditChecker()
                        }
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.QuestionMark,
                        iconResource = R.drawable.ic_tax_faq,
                        title = localizedText("ট্যাক্স FAQ", "Tax FAQ"),
                        description = localizedText(
                            "আয়কর, রিটার্ন, e-TIN, e-Return ও রেয়াতের উত্তর এক জায়গায়।",
                            "Answers on income tax, returns, e-TIN, e-Return, and rebate in one place."
                        ),
                        badge = localizedText(
                            "${faqCount.toString().toBanglaDigits()}টি প্রশ্ন",
                            "$faqCount questions"
                        ),
                        accent = Color(0xFF7C3AED),
                        accentBackground = HomeSoftPurple,
                        onClick = {
                            FirebaseTracker.logHomeServiceOpened("tax_faq")
                            onOpenTaxFaq()
                        }
                    )
                }
                QuickNoticeShortcut(
                    noticeCount = homeNews.notices.size,
                    onClick = onOpenNotices
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                HomeSectionHeader(
                    icon = Icons.Default.AccountBalance,
                    title = localizedText("গুরুত্বপূর্ণ সরকারি লিংক", "Important Govt. Links"),
                    subtitle = localizedText(
                        "সরকারি NBR ও সংশ্লিষ্ট সেবা সরাসরি খুলুন।",
                        "Open official NBR and government services directly."
                    )
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
            }
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
            .padding(
                start = 16.dp,
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 10.dp,
                end = 16.dp,
                bottom = 12.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(CalculatorHeroMiddle, Color(0xFF22A85A))
                        ),
                        shape = RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "%",
                    color = Color.White,
                    fontSize = 27.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Tax Calculator BD",
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HomeActionBlueDark,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1
                )
                Text(
                    text = localizedText(
                        "বাংলাদেশের আয়কর সহায়ক",
                        "Bangladesh income tax helper"
                    ),
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = HomeTextMuted,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1
                )
            }
        }
        Box {
            Surface(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .noRippleClickable(onClick = onNotificationsClick),
                color = bellBackground,
                shape = CircleShape,
                border = BorderStroke(1.dp, HomeBorder)
            ) {
                Box(
                    modifier = Modifier.size(50.dp),
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
private fun TaxCalculatorHero(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CalculatorHeroStart,
                        CalculatorHeroMiddle,
                        Color(0xFF15944B)
                    )
                )
            )
            .noRippleClickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 44.dp, y = (-34).dp)
                .size(170.dp)
                .background(Color(0x2AFFD54F), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 64.dp, y = 34.dp)
                .size(180.dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.13f),
                    shape = RoundedCornerShape(999.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = localizedText(
                                "১০০% অফলাইন ও ব্যক্তিগত",
                                "100% offline & private"
                            ),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                }
                Surface(
                    color = Color.White.copy(alpha = 0.13f),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(15.dp).size(27.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = localizedText("কর হিসাব হোক সহজ", "Make tax feel simple"),
                    color = Color.White,
                    fontSize = 29.sp,
                    lineHeight = 36.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = localizedText(
                        "আয়, করমুক্ত আয় ও বিনিয়োগ যোগ করে কয়েক মিনিটেই পরিষ্কার হিসাব পান।",
                        "Add income, exemptions, and investments to get a clear estimate in minutes."
                    ),
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    lineHeight = 21.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.96f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 18.dp, top = 2.dp, end = 10.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localizedText("হিসাব শুরু করুন", "Start calculating"),
                        color = Color(0xFF0B5C35),
                        fontSize = 17.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Surface(
                        color = Color(0xFFE6F2EC),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF0B6A3C),
                            modifier = Modifier.padding(12.dp).size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FirebaseNewsTicker(
    news: FirebaseHomeNews
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        color = CalculatorPanel,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, CalculatorSuccess.copy(alpha = 0.22f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Surface(
                color = CalculatorSuccess,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = localizedText("NBR খবর", "NBR NEWS"),
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1
                )
            }
            Text(
                text = localizedText(news.banglaText, news.englishText),
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        initialDelayMillis = 700,
                        repeatDelayMillis = 900
                    ),
                color = HomeTextPrimary,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = TiroBanglaFontFamily,
                maxLines = 1
            )
        }
    }
}


@Composable
private fun HomeSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = HomeSoftGreen,
            shape = RoundedCornerShape(13.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = CalculatorSuccess,
                modifier = Modifier.padding(10.dp).size(20.dp)
            )
        }
        SectionTitle(title = title, subtitle = subtitle)
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconResource: Int? = null,
    title: String,
    description: String,
    badge: String,
    accent: Color,
    accentBackground: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(220.dp)
            .noRippleClickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = accentBackground, shape = RoundedCornerShape(14.dp)) {
                    if (iconResource != null) {
                        Image(
                            painter = painterResource(iconResource),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp).size(36.dp)
                        )
                    } else {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.padding(12.dp).size(22.dp)
                        )
                    }
                }
                Surface(color = accentBackground, shape = CircleShape) {
                    Icon(
                        Icons.Default.ArrowOutward,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.padding(8.dp).size(18.dp)
                    )
                }
            }
            Text(
                text = title,
                color = HomeTextPrimary,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = description,
                color = HomeTextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontFamily = TiroBanglaFontFamily
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(color = accentBackground, shape = RoundedCornerShape(12.dp)) {
                Text(
                    text = badge,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = accent,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun QuickNoticeShortcut(
    noticeCount: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .noRippleClickable(onClick = onClick),
        color = CalculatorPanel,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CalculatorSuccess.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Surface(
                color = HomeSoftGreen,
                shape = RoundedCornerShape(13.dp)
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    tint = CalculatorSuccess,
                    modifier = Modifier.padding(11.dp).size(21.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = localizedText("NBR নোটিশ", "NBR Notices"),
                    color = HomeTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = localizedText(
                        "${noticeCount.toString().toBanglaDigits()}টি সর্বশেষ অফিসিয়াল আয়কর নোটিশ",
                        "$noticeCount latest official income-tax notices"
                    ),
                    color = HomeTextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    fontFamily = TiroBanglaFontFamily,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Surface(
                color = CalculatorAccentSoft,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.ArrowOutward,
                    contentDescription = localizedText("নোটিশ খুলুন", "Open notices"),
                    tint = HomeActionBlue,
                    modifier = Modifier.padding(9.dp).size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun LawyerConsultationShortcut(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF123E27),
                            CalculatorHeroMiddle,
                            Color(0xFF13864A)
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 34.dp, y = (-48).dp)
                    .size(150.dp)
                    .background(Color(0x24FFD54F), CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 62.dp, y = 62.dp)
                    .size(150.dp)
                    .background(Color.White.copy(alpha = 0.06f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f))
                    ) {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(14.dp).size(27.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = Color(0xFFFFD54F),
                            shape = RoundedCornerShape(999.dp)
                        ) {
                            Text(
                                text = localizedText("নতুন সেবা", "NEW SERVICE"),
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                                color = Color(0xFF17351F),
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = TiroBanglaFontFamily
                            )
                        }
                        Text(
                            text = localizedText("ট্যাক্স আইনজীবীর পরামর্শ", "Tax Lawyer Consultation"),
                            color = Color.White,
                            fontSize = 18.sp,
                            lineHeight = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = TiroBanglaFontFamily,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = localizedText(
                                "আয়কর · ভ্যাট · কোম্পানি আইন",
                                "Tax · VAT · Company law"
                            ),
                            color = Color.White.copy(alpha = 0.76f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                }

                Text(
                    text = localizedText(
                        "সুবিধামতো তারিখ ও সময় দিয়ে WhatsApp-এ পরামর্শের অনুরোধ পাঠান।",
                        "Choose a convenient date and time, then send your request through WhatsApp."
                    ),
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = TiroBanglaFontFamily
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White.copy(alpha = 0.96f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 15.dp, top = 2.dp, end = 8.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = localizedText("পরামর্শের অনুরোধ করুন", "Request consultation"),
                            color = Color(0xFF0B5C35),
                            fontSize = 14.sp,
                            lineHeight = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = TiroBanglaFontFamily
                        )
                        Surface(color = Color(0xFFE6F2EC), shape = CircleShape) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = localizedText(
                                    "আইনজীবী সেবা খুলুন",
                                    "Open lawyer service"
                                ),
                                tint = Color(0xFF0B6A3C),
                                modifier = Modifier.padding(9.dp).size(19.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class GovtLink(
    val banglaTitle: String,
    val englishTitle: String,
    val url: String,
    val analyticsName: String,
    val icon: ImageVector
)

private val importantGovtLinks = listOf(
    GovtLink("e-TDS", "e-TDS", "https://etds.gov.bd", "e_tds", Icons.Default.Description),
    GovtLink("e-TIN", "e-TIN", "https://secure.incometax.gov.bd/TINHome", "e_tin", Icons.Default.Badge),
    GovtLink("a-Challan", "a-Challan", "https://ibas.finance.gov.bd", "a_challan", Icons.AutoMirrored.Filled.ReceiptLong),
    GovtLink("NBR Website", "NBR Website", "https://nbr.gov.bd", "nbr_website", Icons.Default.AccountBalance),
    GovtLink(
        "সোনালী ব্যাংক পেমেন্ট",
        "Sonali Bank Payment",
        "https://nbr.sblesheba.com/IncomeTax/Payment",
        "sonali_bank_payment",
        Icons.Default.CreditCard
    ),
    GovtLink("e-Return", "e-Return", "https://etaxnbr.gov.bd", "e_return", Icons.Default.UploadFile),
    GovtLink("রিটার্ন যাচাই", "Return Verify", "https://etaxnbr.gov.bd", "return_verify", Icons.Default.Verified)
)

@Composable
private fun ImportantGovtLinksCard(
    onOpenLink: (GovtLink) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        importantGovtLinks.chunked(2).forEach { rowLinks ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowLinks.forEach { link ->
                    GovtLinkChip(
                        modifier = Modifier.weight(1f),
                        link = link,
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

@Composable
private fun GovtLinkChip(
    modifier: Modifier = Modifier,
    link: GovtLink,
    onClick: () -> Unit
) {
    val accent = when (link.analyticsName) {
        "e_tds", "sonali_bank_payment" -> Color(0xFF3974D8)
        "e_tin", "e_return" -> Color(0xFF7C3AED)
        "a_challan" -> Color(0xFFD28B12)
        else -> CalculatorSuccess
    }

    Surface(
        modifier = modifier
            .height(62.dp)
            .noRippleClickable(onClick = onClick),
        color = CalculatorPanel,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = accent.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    link.icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.padding(9.dp).size(18.dp)
                )
            }
            Text(
                text = localizedText(link.banglaTitle, link.englishTitle),
                color = HomeTextPrimary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = TiroBanglaFontFamily,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(18.dp, RoundedCornerShape(34.dp)),
            color = CalculatorPanel.copy(alpha = 0.96f),
            shape = RoundedCornerShape(34.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 7.dp),
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
            HomeSoftGreen
        } else {
            Color.Transparent
        },
        animationSpec = tween(durationMillis = 220),
        label = "bottomNavBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) {
            HomeActionBlue
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
                HomeActionBlue.copy(alpha = 0.08f)
            } else {
                Color.Transparent
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
