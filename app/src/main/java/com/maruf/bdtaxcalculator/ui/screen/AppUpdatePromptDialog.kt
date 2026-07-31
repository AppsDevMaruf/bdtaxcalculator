package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.maruf.bdtaxcalculator.play.AppUpdatePromptState
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable

@Composable
fun AppUpdatePromptDialog(
    state: AppUpdatePromptState,
    onMaybeLater: () -> Unit,
    onUpdateNow: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (!state.isForce) onMaybeLater()
        },
        properties = DialogProperties(
            dismissOnBackPress = !state.isForce,
            dismissOnClickOutside = !state.isForce
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, CalculatorBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UpdateDragHandle()
                Spacer(modifier = Modifier.height(28.dp))

                UpdateHeroIcon(force = state.isForce)
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (state.isForce) {
                        localizedText("আপডেট আবশ্যক", "Update Required")
                    } else {
                        localizedText("নতুন সংস্করণ পাওয়া গেছে!", "New version available!")
                    },
                    color = HomeTextPrimary,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    fontFamily = TiroBanglaFontFamily
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (state.isForce) {
                        localizedText(
                            "নতুন ফিচার, নিরাপত্তা এবং স্থিতিশীলতার জন্য সর্বশেষ সংস্করণে আপডেট করুন। মাত্র এক মিনিট লাগবে।",
                            "To keep the app secure and enjoy the latest features, please update to the newest version. It only takes a minute!"
                        )
                    } else {
                        localizedText(
                            "অর্থ আইন ২০২৬ অনুযায়ী কর হিসাব, tax credit এবং FAQ আপডেট করা হয়েছে।",
                            "Tax calculation, tax credits, and FAQs are updated under the Finance Act 2026."
                        )
                    },
                    color = CalculatorMuted,
                    fontSize = 18.sp,
                    lineHeight = 27.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = TiroBanglaFontFamily
                )

                if (state.isForce) {
                    ForceUpdateDetails()
                }

                Spacer(modifier = Modifier.height(26.dp))
                HorizontalDivider(color = CalculatorBorder)
                Spacer(modifier = Modifier.height(14.dp))

                if (state.isForce) {
                    PrimaryUpdateButton(
                        text = localizedText("সর্বশেষ সংস্করণ নিন", "Get latest version"),
                        onClick = onUpdateNow
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = localizedText(
                            "বর্তমান সংস্করণ: ${state.currentVersionName}",
                            "Current Version: ${state.currentVersionName}"
                        ),
                        color = CalculatorMuted,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = TiroBanglaFontFamily
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SecondaryUpdateButton(
                            modifier = Modifier.weight(1f),
                            text = localizedText("পরে করব", "Maybe Later"),
                            onClick = onMaybeLater
                        )
                        PrimaryUpdateButton(
                            modifier = Modifier.weight(1f),
                            text = localizedText("এখন আপডেট", "Update Now"),
                            onClick = onUpdateNow
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateDragHandle() {
    Box(
        modifier = Modifier
            .size(width = 74.dp, height = 7.dp)
            .background(CalculatorBorder, RoundedCornerShape(999.dp))
    )
}

@Composable
private fun UpdateHeroIcon(force: Boolean) {
    Surface(
        modifier = Modifier.size(104.dp),
        color = CalculatorAccentSoft,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier.size(78.dp),
                color = CalculatorSuccess.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (force) Icons.Default.KeyboardDoubleArrowUp else Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = CalculatorSuccess,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ForceUpdateDetails() {
    Spacer(modifier = Modifier.height(26.dp))
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CalculatorSurfaceAlt,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = localizedText("এই আপডেটে যা আছে", "What's inside"),
                color = HomeTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            UpdateBullet(localizedText("২০২৫-২৬ আয়বর্ষের সংশোধিত কর হিসাব", "Corrected tax calculation for income year 2025-26"))
            UpdateBullet(localizedText("গত ৫ আয়বর্ষের করের পার্থক্য দেখুন", "Compare tax across the last five income years"))
            UpdateBullet(localizedText("বিনিয়োগ রেয়াতের নতুন খাত, সহজ নির্বাচন ও সরকারি তথ্যসূত্র", "New rebate categories, easier selection, and official data sources"))
        }
    }
}

@Composable
private fun UpdateBullet(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(26.dp),
            color = CalculatorSuccess,
            shape = CircleShape
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(5.dp)
            )
        }
        Text(
            text = text,
            color = HomeTextPrimary,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun PrimaryUpdateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .noRippleClickable(onClick = onClick),
        color = CalculatorSuccess,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun SecondaryUpdateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(58.dp)
            .noRippleClickable(onClick = onClick),
        color = CalculatorPanel,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = CalculatorMuted,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
