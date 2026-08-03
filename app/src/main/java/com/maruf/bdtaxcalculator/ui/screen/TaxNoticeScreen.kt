package com.maruf.bdtaxcalculator.ui.screen

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseHomeNewsStore
import com.maruf.bdtaxcalculator.firebase.FirebaseTaxNotice
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.HomeActionBlue
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable

@Composable
fun TaxNoticeScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val homeNews by FirebaseHomeNewsStore.observe().collectAsState()

    LaunchedEffect(Unit) {
        FirebaseHomeNewsStore.refresh()
    }

    Scaffold(
        topBar = {
            NoticeTopBar(onBack = onBack)
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        LazyColumn(
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
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = FloatingBottomBarSafePadding
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                NoticeIntro(count = homeNews.notices.size)
            }
            itemsIndexed(
                items = homeNews.notices,
                key = { _, notice -> notice.id }
            ) { index, notice ->
                TaxNoticeCard(
                    serial = index + 1,
                    notice = notice,
                    onClick = {
                        FirebaseTracker.logEvent(
                            "tax_notice_opened",
                            Bundle().apply { putString("notice_id", notice.id) }
                        )
                        uriHandler.openUri(notice.url)
                    }
                )
            }
        }
    }
}

@Composable
private fun NoticeTopBar(onBack: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.noRippleClickable(onClick = onBack),
                color = CalculatorAccentSoft,
                shape = CircleShape,
                border = BorderStroke(1.dp, CalculatorBorder)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = localizedText("পেছনে যান", "Go back"),
                    tint = HomeActionBlue,
                    modifier = Modifier.padding(11.dp).size(21.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = localizedText("আয়কর নোটিশ", "Income Tax Notices"),
                    color = HomeTextPrimary,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = localizedText("জাতীয় রাজস্ব বোর্ড", "National Board of Revenue"),
                    color = CalculatorMuted,
                    fontSize = 10.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun NoticeIntro(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = HomeSoftGreen, shape = RoundedCornerShape(14.dp)) {
            Icon(
                Icons.Default.NotificationsNone,
                contentDescription = null,
                tint = CalculatorSuccess,
                modifier = Modifier.padding(11.dp).size(22.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = localizedText("সর্বশেষ সরকারি নোটিশ", "Latest official notices"),
                color = HomeTextPrimary,
                fontSize = 17.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText(
                    "${count.toString().toNoticeBanglaDigits()}টি প্রকাশিত নোটিশ",
                    "$count published notices"
                ),
                color = CalculatorMuted,
                fontSize = 11.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun TaxNoticeCard(
    serial: Int,
    notice: FirebaseTaxNotice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(HomeSoftGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = localizedText(serial.toString().toNoticeBanglaDigits(), serial.toString()),
                    color = CalculatorSuccess,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    text = localizedText(notice.banglaTitle, notice.englishTitle),
                    color = HomeTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localizedText(
                            "প্রকাশ: ${notice.publishedDate.toNoticeBanglaDigits()}",
                            "Published: ${notice.publishedDate}"
                        ),
                        color = CalculatorMuted,
                        fontSize = 10.sp,
                        fontFamily = TiroBanglaFontFamily
                    )
                    Surface(
                        color = CalculatorAccentSoft,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = localizedText("বিস্তারিত", "Details"),
                                color = HomeActionBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = TiroBanglaFontFamily
                            )
                            Icon(
                                Icons.Default.ArrowOutward,
                                contentDescription = null,
                                tint = HomeActionBlue,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun String.toNoticeBanglaDigits(): String = map { char ->
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
