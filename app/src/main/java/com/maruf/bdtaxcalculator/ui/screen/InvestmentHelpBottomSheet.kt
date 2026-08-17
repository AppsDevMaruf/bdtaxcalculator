package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInk
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily

internal data class InvestmentHelpContent(
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val noteBn: String? = null,
    val noteEn: String? = null
)

internal object InvestmentHelpContents {
    val gpf = InvestmentHelpContent(
        titleBn = "GPF কী?",
        titleEn = "What is GPF?",
        descriptionBn = "এখানে চলতি আয় বছরে আপনার নিজের জমা দেওয়া মোট GPF (General Provident Fund) লিখবেন।",
        descriptionEn = "Enter your total own contribution to GPF (General Provident Fund) for the current income year.",
        noteBn = "মাসিক জমার পরিমাণ দিলে আগে বার্ষিক মোট পরিমাণ হিসাব করে লিখুন।",
        noteEn = "If you know the monthly contribution, calculate and enter the annual total first."
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InvestmentHelpBottomSheet(
    content: InvestmentHelpContent,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CalculatorPanel,
        contentColor = CalculatorInk,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 4.dp)
                    .size(width = 38.dp, height = 4.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = CalculatorMuted.copy(alpha = 0.35f),
                    shape = CircleShape
                ) {}
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = CalculatorAccentSoft,
                border = BorderStroke(1.dp, CalculatorBorder)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(25.dp),
                        tint = CalculatorSuccess
                    )
                }
            }
            Text(
                text = localizedText(content.titleBn, content.titleEn),
                color = CalculatorInk,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText(content.descriptionBn, content.descriptionEn),
                color = CalculatorMuted,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontFamily = TiroBanglaFontFamily
            )
            if (content.noteBn != null && content.noteEn != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = CalculatorAccentSoft,
                    border = BorderStroke(1.dp, CalculatorBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(13.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = CalculatorSuccess
                        )
                        Text(
                            localizedText(content.noteBn, content.noteEn),
                            modifier = Modifier.weight(1f),
                            color = CalculatorInk,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    localizedText("বুঝেছি", "Got it"),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}
