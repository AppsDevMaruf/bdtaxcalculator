package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import com.maruf.bdtaxcalculator.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily

/** A passive, dismissible next step beneath the final tax result. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun LawyerConsultationBanner(onOpen: () -> Unit, onDismiss: () -> Unit) {
    // App theme can differ from the phone's system theme.
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val background = if (dark) Color(0xFF172B20) else Color(0xFFF2F8F3)
    val foreground = if (dark) Color(0xFFEDF7EF) else Color(0xFF173421)
    val secondary = if (dark) Color(0xFFB8CDBF) else Color(0xFF53665A)
    val accent = if (dark) Color(0xFF91D6A2) else Color(0xFF1D7C38)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = background,
        contentColor = foreground,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = accent.copy(alpha = 0.12f)) {
                    Icon(
                        painterResource(R.drawable.ic_lawyer_consultation),
                        contentDescription = null, tint = accent,
                        modifier = Modifier.padding(8.dp).size(36.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        localizedText("ট্যাক্স আইনজীবীর সহায়তা", "Tax lawyer assistance"),
                        color = foreground, fontFamily = TiroBanglaFontFamily,
                        fontSize = 15.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        localizedText("পেইড পরামর্শ", "Paid consultation"),
                        color = secondary, fontFamily = TiroBanglaFontFamily,
                        fontSize = 11.sp, lineHeight = 16.sp
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = localizedText("পরামর্শের প্রস্তাব বন্ধ করুন", "Dismiss consultation suggestion"),
                        tint = secondary, modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                localizedText(
                    "রিটার্ন জমা দিতে সাহায্য দরকার? আপনার হিসাব নিয়ে ট্যাক্স আইনজীবীর পরামর্শ নিন।",
                    "Need help filing your return? Discuss your calculation with a tax lawyer."
                ),
                color = secondary, fontFamily = TiroBanglaFontFamily,
                fontSize = 13.sp, lineHeight = 20.sp
            )
            // Wrap actions at large font scales rather than squeezing the labels.
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = onOpen, modifier = Modifier.heightIn(min = 48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1D7C38), contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        localizedText("আইনজীবী দেখুন", "View lawyers"),
                        fontFamily = TiroBanglaFontFamily, fontSize = 13.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp))
                }
                TextButton(onClick = onDismiss, modifier = Modifier.heightIn(min = 48.dp)) {
                    Text(
                        localizedText("এখন নয়", "Not now"),
                        color = accent, fontFamily = TiroBanglaFontFamily, fontSize = 13.sp
                    )
                }
            }
        }
    }
}
