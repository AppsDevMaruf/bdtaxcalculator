package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.nbr.NbrTinLookupRepository
import com.maruf.bdtaxcalculator.nbr.NbrTinLookupResult
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDangerSoft2
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeSoftGreen
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NbrTinCheckScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var tinInput by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<NbrTinLookupResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun lookupTin() {
        if (isLoading || tinInput.length != TinInputLength) return

        focusManager.clearFocus()
        keyboardController?.hide()
        result = null
        errorMessage = null
        isLoading = true

        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    NbrTinLookupRepository.lookup(tinInput)
                }
            }.onSuccess { lookupResult ->
                FirebaseTracker.logEvent("nbr_tin_lookup_completed")
                result = lookupResult
            }.onFailure { throwable ->
                FirebaseTracker.logEvent("nbr_tin_lookup_failed")
                errorMessage = throwable.message ?: "TIN lookup failed."
            }
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp)
            .padding(bottom = FloatingBottomBarSafePadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TinLookupHeader()
        PrivacyNoticeCard()
        TinLookupInputCard(
            tinInput = tinInput,
            isLoading = isLoading,
            canSearch = tinInput.length == TinInputLength,
            onTinChange = { value ->
                tinInput = normalizeAuditTinInput(value)
                result = null
                errorMessage = null
            },
            onSearch = ::lookupTin
        )

        errorMessage?.let { message ->
            TinLookupMessageCard(
                message = localizedText(
                    "TIN তথ্য পাওয়া যায়নি বা NBR সার্ভারে সমস্যা হয়েছে। একটু পরে আবার চেষ্টা করুন।",
                    message
                ),
                isError = true
            )
        }

        result?.let { lookupResult ->
            TinLookupResultCard(result = lookupResult)
        }

        if (result == null && errorMessage == null) {
            TinLookupGuideCard()
        }
    }
}
@Composable
private fun TinLookupHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(color = HomeSoftGreen, shape = CircleShape) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = CalculatorSuccess,
                modifier = Modifier.padding(12.dp).size(24.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = localizedText("NBR TIN যাচাই", "NBR TIN Check"),
                color = HomeTextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText(
                    "TIN দিলে সরকারি পোর্টাল থেকে তথ্য দেখাবে",
                    "Enter a TIN to fetch details from the official portal"
                ),
                color = CalculatorMuted,
                fontSize = 13.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
@Composable
private fun PrivacyNoticeCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = CalculatorSuccess,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = localizedText(
                    "এই lookup সরাসরি NBR-Sonali সরকারি endpoint-এ request পাঠায়। অ্যাপ আপনার TIN বা result সংরক্ষণ করে না।",
                    "This lookup sends the request directly to the official NBR-Sonali endpoint. The app does not store your TIN or result."
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
private fun TinLookupInputCard(
    tinInput: String,
    isLoading: Boolean,
    canSearch: Boolean,
    onTinChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = localizedText("TIN নম্বর", "TIN Number"),
                color = HomeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            OutlinedTextField(
                value = tinInput,
                onValueChange = onTinChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = CalculatorSuccess
                    )
                },
                placeholder = {
                    Text(
                        localizedText("১২ সংখ্যার TIN লিখুন", "Enter 12-digit TIN"),
                        fontFamily = TiroBanglaFontFamily
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = CalculatorFieldText,
                    fontFamily = TiroBanglaFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CalculatorSuccess,
                    unfocusedBorderColor = CalculatorBorder,
                    focusedContainerColor = CalculatorSurfaceAlt,
                    unfocusedContainerColor = CalculatorSurfaceAlt,
                    disabledContainerColor = CalculatorSurfaceAlt
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = localizedText(
                        "${tinInput.length}/১২ সংখ্যা",
                        "${tinInput.length}/12 digits"
                    ),
                    color = CalculatorMuted,
                    fontSize = 12.sp,
                    fontFamily = TiroBanglaFontFamily
                )
                Button(
                    enabled = canSearch && !isLoading,
                    onClick = onSearch
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = localizedText("চেক করুন", "Check"),
                        fontFamily = TiroBanglaFontFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TinLookupResultCard(result: NbrTinLookupResult) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, CalculatorSuccess.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = CalculatorSuccess
                )
                Text(
                    text = localizedText("TIN তথ্য পাওয়া গেছে", "TIN details found"),
                    color = HomeTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = TiroBanglaFontFamily
                )
            }

            ResultRow(localizedText("TIN", "TIN"), result.tin)
            ResultRow(localizedText("নাম", "Name"), result.applicantName)
            ResultRow(localizedText("ঠিকানা", "Address"), result.address)
            ResultRow(localizedText("ট্যাক্স জোন", "Tax Zone"), result.taxZone)
            ResultRow(localizedText("ট্যাক্স সার্কেল", "Tax Circle"), result.taxCircle)
            ResultRow(localizedText("অ্যাকাউন্ট কোড", "Account Code"), result.accountCode)
            if (result.typeOfTin.isNotBlank()) {
                ResultRow(localizedText("TIN ধরন", "TIN Type"), result.typeOfTin)
            }
        }
    }
}
@Composable
private fun ResultRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = CalculatorMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = TiroBanglaFontFamily
        )
        Text(
            text = value.ifBlank { localizedText("উল্লেখ নেই", "Not available") },
            color = HomeTextPrimary,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun TinLookupMessageCard(message: String, isError: Boolean) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isError) CalculatorDangerSoft2 else HomeSoftGreen
        ),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, if (isError) CalculatorDanger.copy(alpha = 0.3f) else CalculatorSuccess.copy(alpha = 0.3f))
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            color = if (isError) CalculatorDanger else CalculatorSuccess,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
    }
}

@Composable
private fun TinLookupGuideCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CalculatorBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = localizedText("কীভাবে কাজ করে", "How it works"),
                color = HomeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                text = localizedText(
                    "১. TIN দিলে app fresh NBR session token নেয়।\n২. Official search request পাঠায়।\n৩. পাওয়া গেলে নাম, ঠিকানা, জোন, সার্কেল ও অ্যাকাউন্ট কোড দেখায়।",
                    "1. The app gets a fresh NBR session token.\n2. It sends the official search request.\n3. If found, it shows name, address, zone, circle, and account code."
                ),
                color = CalculatorMuted,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
