package com.maruf.bdtaxcalculator.ui.screen

import android.os.Bundle
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.ui.content.NbrFaqSourceUrl
import com.maruf.bdtaxcalculator.ui.content.TaxFaq
import com.maruf.bdtaxcalculator.ui.content.incomeTaxFaqs
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMutedSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.HomeBorder
import com.maruf.bdtaxcalculator.ui.theme.HomeTextMuted
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import com.maruf.utils.noRippleClickable
import java.text.Normalizer
import java.util.Locale

@Composable
fun TaxFaqScreen(
    onBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val faqs = incomeTaxFaqs()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var expandedFaqId by rememberSaveable { mutableStateOf<String?>(null) }
    val visibleFaqs = if (searchQuery.isBlank()) {
        faqs
    } else {
        faqs.filter { faq -> faq.matches(searchQuery) }
    }

    Scaffold(
        topBar = {
            TaxFaqTopBar(onBack = onBack)
        },
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            FaqStickySearchHeader(
                searchQuery = searchQuery,
                visibleCount = visibleFaqs.size,
                onSearchChange = {
                    searchQuery = it
                    expandedFaqId = null
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (visibleFaqs.isEmpty()) {
                    item { EmptyFaqSearchState() }
                } else {
                    items(
                        items = visibleFaqs,
                        key = { it.id }
                    ) { faq ->
                        FaqItem(
                            faq = faq,
                            isExpanded = expandedFaqId == faq.id,
                            onToggle = {
                                expandedFaqId = if (expandedFaqId == faq.id) null else faq.id
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqStickySearchHeader(
    searchQuery: String,
    visibleCount: Int,
    onSearchChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FaqSearchField(
                value = searchQuery,
                onValueChange = onSearchChange
            )
            Text(
                text = localizedText(
                    "${visibleCount.toString().toBanglaDigits()}টি প্রশ্ন পাওয়া গেছে",
                    "$visibleCount questions found"
                ),
                color = CalculatorMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun TaxFaqTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderActionButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            label = localizedText("ফিরুন", "Back"),
            onClick = onBack
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = localizedText("আয়কর সংক্রান্ত সচরাচর জিজ্ঞাসা", "Tax FAQ"),
                color = HomeTextPrimary,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText("NBR income tax প্রশ্নোত্তর", "NBR income tax Q&A"),
                color = HomeTextMuted,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}

@Composable
private fun HeaderActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Box(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(9.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = CalculatorSuccess, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun TaxFaqIntroCard(
    totalFaqs: Int,
    onOpenSource: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, HomeBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    text = localizedText("আয়কর সংক্রান্ত সচরাচর জিজ্ঞাসা", "Frequently Asked Questions"),
                    color = HomeTextPrimary,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
                Text(
                    text = localizedText(
                        "NBR FAQ থেকে আয়কর প্রশ্নগুলোর offline summary।",
                        "Offline summaries of Income Tax questions from the NBR FAQ."
                    ),
                    color = HomeTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ) {
                        Text(
                            text = localizedText("আয়কর", "Income Tax"),
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                    TextButton(onClick = onOpenSource) {
                        Text(
                            text = localizedText("NBR source", "NBR source"),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = TiroBanglaFontFamily
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Text(
                    text = localizedText(
                        "${totalFaqs.toString().toBanglaDigits()}টি প্রশ্ন · সব data app-এর ভিতরেই থাকে",
                        "$totalFaqs questions · all data stays inside the app"
                    ),
                    color = CalculatorMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

@Composable
private fun FaqSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    color = HomeTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = TiroBanglaFontFamily
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isBlank()) {
                            Text(
                                text = localizedText("প্রশ্ন খুঁজুন", "Search question"),
                                color = CalculatorMutedSoft,
                                fontSize = 14.sp,
                                fontFamily = TiroBanglaFontFamily
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyFaqSearchState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CalculatorPanel,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Text(
            text = localizedText(
                "এই keyword দিয়ে কোনো প্রশ্ন পাওয়া যায়নি।",
                "No question matched this keyword."
            ),
            modifier = Modifier.padding(14.dp),
            color = CalculatorMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun FaqItem(
    faq: TaxFaq,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        color = if (isExpanded) CalculatorPanel else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isExpanded) MaterialTheme.colorScheme.primary else CalculatorBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .noRippleClickable(onClick = onToggle)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (isExpanded) localizedText("বন্ধ করুন", "Collapse") else localizedText("খুলুন", "Expand"),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = localizedText(faq.banglaQuestion, faq.englishQuestion),
                    modifier = Modifier.weight(1f),
                    color = HomeTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Text(
                    text = localizedText(faq.banglaAnswer, faq.englishAnswer),
                    color = CalculatorMuted,
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    }
}

private fun TaxFaq.matches(query: String): Boolean {
    val tokens = query.searchNormalized().split(" ").filter { it.isNotBlank() }
    if (tokens.isEmpty()) return true

    val searchableText = listOf(banglaQuestion, englishQuestion, banglaAnswer, englishAnswer)
        .joinToString(" ")
        .searchNormalized()

    return tokens.all { token -> searchableText.contains(token) }
}

private fun String.searchNormalized(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFC)
        .replace("\u09AF\u09BC", "\u09DF")
        .replace("\u09A1\u09BC", "\u09DC")
        .replace("\u09A2\u09BC", "\u09DD")
        .replace("\u200C", "")
        .replace("\u200D", "")
        .lowercase(Locale.ROOT)
        .replace(Regex("[^\\p{L}\\p{N}]+"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()
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
