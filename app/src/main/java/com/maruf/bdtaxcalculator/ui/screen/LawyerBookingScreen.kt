package com.maruf.bdtaxcalculator.ui.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker
import com.maruf.bdtaxcalculator.lawyer.BookingStatus
import com.maruf.bdtaxcalculator.lawyer.ConsultationMode
import com.maruf.bdtaxcalculator.lawyer.LawyerBookingRequest
import com.maruf.bdtaxcalculator.lawyer.LawyerProfile
import com.maruf.bdtaxcalculator.lawyer.LegalServiceCategory
import com.maruf.bdtaxcalculator.lawyer.LocalLawyerBookingRepository
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorAccentSoft
import com.maruf.bdtaxcalculator.ui.theme.CalculatorBorder
import com.maruf.bdtaxcalculator.ui.theme.CalculatorDanger
import com.maruf.bdtaxcalculator.ui.theme.CalculatorFieldText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorHeroMiddle
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorPanel
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSurfaceAlt
import com.maruf.bdtaxcalculator.ui.theme.HomeTextPrimary
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.core.net.toUri
private enum class LawyerScreenTab { Lawyers, Requests }
private const val CONSULTATION_WHATSAPP_NUMBER = "8801687422428"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LawyerBookingScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val repository = remember(context) { LocalLawyerBookingRepository(context) }
    val lawyers by repository.observeLawyers().collectAsState(initial = emptyList())
    val requests by repository.observeRequests().collectAsState(initial = emptyList())
    var selectedTab by rememberSaveable { mutableStateOf(LawyerScreenTab.Lawyers) }
    var showRequestForm by rememberSaveable { mutableStateOf(false) }
    val whatsappOpenError = localizedText(
        "WhatsApp বা browser খোলা যায়নি। অনুরোধটি খসড়া হিসেবে রাখা হয়েছে।",
        "WhatsApp or a browser could not be opened. The request was saved as a draft."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (showRequestForm) {
                                localizedText("পরামর্শের অনুরোধ", "Consultation request")
                            } else {
                                localizedText("ট্যাক্স আইনজীবী", "Tax Lawyer Consultation")
                            },
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = TiroBanglaFontFamily,
                            maxLines = 1
                        )
                        Text(
                            if (showRequestForm) {
                                localizedText("তথ্য পূরণ করে WhatsApp-এ পাঠান", "Complete the details and send via WhatsApp")
                            } else {
                                localizedText("আয়কর · ভ্যাট · কোম্পানি আইন", "Tax · VAT · Company law")
                            },
                            color = CalculatorMuted,
                            fontSize = 11.sp,
                            fontFamily = TiroBanglaFontFamily,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = if (showRequestForm) ({ showRequestForm = false }) else onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = localizedText("ফিরে যান", "Back"))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.background, CalculatorAccentSoft)
                    )
                )
                .padding(innerPadding)
                .pointerInput(focusManager, keyboardController) {
                    detectTapGestures {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
        ) {
            if (!showRequestForm) {
                ServiceSafetyBanner()
                PrimaryTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = CalculatorSuccess
                ) {
                    Tab(
                        selected = selectedTab == LawyerScreenTab.Lawyers,
                        onClick = {
                            selectedTab = LawyerScreenTab.Lawyers
                            showRequestForm = false
                        },
                        text = { Text(localizedText("আইনজীবী", "Lawyers"), fontFamily = TiroBanglaFontFamily) },
                        icon = { Icon(Icons.Default.PersonSearch, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == LawyerScreenTab.Requests,
                        onClick = {
                            selectedTab = LawyerScreenTab.Requests
                            showRequestForm = false
                        },
                        text = { Text(localizedText("আমার অনুরোধ (${requests.size})", "My requests (${requests.size})"), fontFamily = TiroBanglaFontFamily) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                    )
                }
            }

            when {
                showRequestForm -> ConsultationRequestForm(
                    onCancel = { showRequestForm = false },
                    onSave = { request ->
                        repository.saveDraft(request)
                        FirebaseTracker.logEvent("lawyer_consultation_draft_saved")
                        if (openConsultationInWhatsApp(context, request)) {
                            FirebaseTracker.logEvent("lawyer_consultation_whatsapp_opened")
                        } else {
                            Toast.makeText(
                                context,
                                whatsappOpenError,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        selectedTab = LawyerScreenTab.Requests
                        showRequestForm = false
                    }
                )
                selectedTab == LawyerScreenTab.Lawyers -> LawyerDirectory(
                    lawyers = lawyers,
                    onRequestConsultation = { showRequestForm = true }
                )
                else -> BookingRequests(
                    requests = requests,
                    onCreate = { showRequestForm = true },
                    onDelete = repository::deleteDraft
                )
            }
        }
    }
}
@Composable
private fun ServiceSafetyBanner() {
    Surface(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        color = CalculatorAccentSoft,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CalculatorSuccess.copy(alpha = 0.24f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = CalculatorSuccess, modifier = Modifier.size(19.dp))
            Text(
                localizedText(
                    "এই প্ল্যাটফর্ম শুধু যোগাযোগ ও বুকিংয়ে সহায়তা করবে; আইনি পরামর্শ সংশ্লিষ্ট আইনজীবী স্বাধীনভাবে দেবেন।",
                    "This platform facilitates contact and booking; legal advice is independently provided by the lawyer."
                ),
                color = CalculatorMuted,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
@Composable
private fun LawyerDirectory(
    lawyers: List<LawyerProfile>,
    onRequestConsultation: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, FloatingBottomBarSafePadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (lawyers.isEmpty()) {
            item {
                EmptyLawyerDirectory(onRequestConsultation)
            }
        } else {
            items(lawyers, key = LawyerProfile::id) { lawyer -> LawyerProfileCard(lawyer) }
        }
    }
}
@Composable
private fun EmptyLawyerDirectory(onRequestConsultation: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        border = BorderStroke(1.dp, CalculatorBorder),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(color = CalculatorAccentSoft, shape = CircleShape) {
                Icon(Icons.Default.Gavel, contentDescription = null, tint = CalculatorSuccess, modifier = Modifier.padding(18.dp).size(32.dp))
            }
            Text(
                localizedText("যাচাইকৃত আইনজীবী যুক্ত করা হচ্ছে", "Verified lawyers are being onboarded"),
                color = HomeTextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                fontFamily = TiroBanglaFontFamily
            )
            Text(
                localizedText(
                    "Bar Council enrolment ও Bar Association membership যাচাই ছাড়া কোনো প্রোফাইল দেখানো হবে না। এখন আপনার প্রয়োজনটি খসড়া হিসেবে সংরক্ষণ করতে পারেন।",
                    "Profiles will appear only after Bar Council enrolment and Bar Association membership checks. You can save your consultation need as a draft now."
                ),
                color = CalculatorMuted,
                fontSize = 13.sp,
                lineHeight = 21.sp,
                fontFamily = TiroBanglaFontFamily
            )
            Button(
                onClick = onRequestConsultation,
                colors = ButtonDefaults.buttonColors(containerColor = CalculatorHeroMiddle),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(localizedText("পরামর্শের অনুরোধ লিখুন", "Create consultation request"), fontFamily = TiroBanglaFontFamily)
            }
        }
    }
}
@Composable
private fun LawyerProfileCard(lawyer: LawyerProfile) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CalculatorPanel),
        border = BorderStroke(1.dp, CalculatorBorder),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Default.Badge, contentDescription = null, tint = CalculatorSuccess)
                Text(lawyer.name, color = HomeTextPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = TiroBanglaFontFamily)
            }
            Text(lawyer.barAssociation, color = CalculatorMuted, fontSize = 12.sp, fontFamily = TiroBanglaFontFamily)
            Text(
                localizedText("অভিজ্ঞতা: ${lawyer.experienceYears} বছর", "Experience: ${lawyer.experienceYears} years"),
                color = CalculatorMuted,
                fontSize = 12.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConsultationRequestForm(
    onCancel: () -> Unit,
    onSave: (LawyerBookingRequest) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var mobile by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var time by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(LegalServiceCategory.IncomeTax) }
    var mode by rememberSaveable { mutableStateOf(ConsultationMode.Online) }
    var hasContactConsent by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showDateRequiredError by rememberSaveable { mutableStateOf(false) }
    val normalizedMobile = mobile.filter(Char::isDigit)
    val isMobileValid = normalizedMobile.matches(Regex("^01[3-9][0-9]{8}$"))
    val normalizedEmail = email.trim()
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(normalizedEmail).matches()
    val requiresEmail = mode == ConsultationMode.Online
    val hasSelectedSchedule = date.isNotBlank() && time.isNotBlank()
    val isScheduleInFuture = !hasSelectedSchedule || isConsultationScheduleInFuture(date, time)
    val canSave = name.trim().length >= 2 &&
        isMobileValid &&
        (!requiresEmail || isEmailValid) &&
        date.isNotBlank() &&
        time.isNotBlank() &&
        isScheduleInFuture &&
        hasContactConsent

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, FloatingBottomBarSafePadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(localizedText("পরামর্শের তথ্য", "Consultation details"), color = HomeTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, fontFamily = TiroBanglaFontFamily)
        }
        item {
            ConsultationTextField(
                value = name,
                onValueChange = { name = it.take(80) },
                label = localizedText("আপনার নাম", "Your name"),
                placeholder = localizedText("আপনার পূর্ণ নাম লিখুন", "Enter your full name")
            )
        }
        item {
            ConsultationTextField(
                value = mobile,
                onValueChange = { mobile = it.filter(Char::isDigit).take(11) },
                label = localizedText("মোবাইল নম্বর", "Mobile number"),
                placeholder = "01XXXXXXXXX",
                supportingText = localizedText("১১ সংখ্যার বাংলাদেশি নম্বর", "11-digit Bangladeshi number"),
                isError = mobile.isNotBlank() && !isMobileValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
        item {
            ConsultationTextField(
                value = email,
                onValueChange = { email = it.take(120) },
                label = localizedText(
                    if (requiresEmail) "ইমেইল (অনলাইন কলে আবশ্যক)" else "ইমেইল (ঐচ্ছিক)",
                    if (requiresEmail) "Email (required for online call)" else "Email (optional)"
                ),
                placeholder = "name@example.com",
                supportingText = if (requiresEmail) localizedText("Meet invitation এই ঠিকানায় যাবে", "The Meet invitation will be sent here") else null,
                isError = email.isNotBlank() && !isEmailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }
        item { ChoiceSection(localizedText("সেবার ধরন", "Legal service"), LegalServiceCategory.entries, category, { category = it }) { localizedText(it.banglaLabel, it.englishLabel) } }
        item { ChoiceSection(localizedText("পরামর্শের মাধ্যম", "Consultation mode"), ConsultationMode.entries, mode, { mode = it }) { localizedText(it.banglaLabel, it.englishLabel) } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ConsultationPickerField(
                    value = date,
                    modifier = Modifier.weight(1f),
                    label = localizedText("তারিখ", "Date"),
                    placeholder = localizedText("তারিখ নির্বাচন করুন", "Select date"),
                    icon = Icons.Default.CalendarMonth,
                    isError = showDateRequiredError,
                    supportingText = if (showDateRequiredError) {
                        localizedText("আগে তারিখ নির্বাচন করুন", "Select a date first")
                    } else {
                        null
                    },
                    onClick = { showDatePicker = true }
                )
                ConsultationPickerField(
                    value = time,
                    modifier = Modifier.weight(1f),
                    label = localizedText("সময়", "Time"),
                    placeholder = localizedText("সময় নির্বাচন করুন", "Select time"),
                    icon = Icons.Default.Schedule,
                    isError = hasSelectedSchedule && !isScheduleInFuture,
                    supportingText = if (hasSelectedSchedule && !isScheduleInFuture) {
                        localizedText(
                            "অতীত সময় নির্বাচন করা যাবে না",
                            "Past time cannot be selected"
                        )
                    } else {
                        null
                    },
                    onClick = {
                        if (date.isBlank()) {
                            showDateRequiredError = true
                        } else {
                            showTimePicker = true
                        }
                    },
                )
            }
        }
        item {
            ConsultationTextField(
                value = note,
                onValueChange = { note = it.take(500) },
                label = localizedText("সমস্যার সংক্ষিপ্ত বিবরণ (ঐচ্ছিক)", "Short description (optional)"),
                placeholder = localizedText("সংক্ষেপে আপনার প্রয়োজন লিখুন", "Briefly describe what you need"),
                singleLine = false,
                minLines = 3
            )
        }
        item {
            Surface(color = CalculatorAccentSoft, shape = RoundedCornerShape(14.dp)) {
                Text(
                    localizedText(
                        "তথ্যটি এই ডিভাইসে সেভ হবে। আপনি এগোলেই শুধু WhatsApp খুলবে।",
                        "Saved on this device. WhatsApp opens only when you continue."
                    ),
                    modifier = Modifier.padding(12.dp),
                    color = CalculatorMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = hasContactConsent,
                    onCheckedChange = { hasContactConsent = it }
                )
                Text(
                    localizedText(
                        "এই পরামর্শের জন্য দেওয়া তথ্য শেয়ার করতে আমি সম্মত।",
                        "I agree to share these details for this consultation."
                    ),
                    modifier = Modifier.padding(top = 11.dp),
                    color = CalculatorMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    enabled = canSave,
                    onClick = {
                        onSave(
                            LocalLawyerBookingRepository.newDraft(
                                clientName = name,
                                mobileNumber = normalizedMobile,
                                clientEmail = normalizedEmail,
                                category = category,
                                mode = mode,
                                preferredDate = date,
                                preferredTime = time,
                                note = note,
                                contactConsentAtMillis = System.currentTimeMillis()
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CalculatorHeroMiddle)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.size(8.dp))
                    Text(
                        localizedText("WhatsApp-এ পাঠান", "Send to WhatsApp"),
                        fontFamily = TiroBanglaFontFamily,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                TextButton(onClick = onCancel) {
                    Text(
                        localizedText("বাতিল", "Cancel"),
                        color = CalculatorMuted,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        ConsultationDatePickerDialog(
            currentValue = date,
            onDismiss = { showDatePicker = false },
            onConfirm = { selectedDate ->
                date = selectedDate
                showDateRequiredError = false
                if (time.isNotBlank() && !isConsultationScheduleInFuture(selectedDate, time)) {
                    time = ""
                }
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        ConsultationTimePickerDialog(
            currentValue = time,
            restrictToFutureTime = date == currentConsultationDate(),
            onDismiss = { showTimePicker = false },
            onConfirm = { selectedTime ->
                time = selectedTime
                showTimePicker = false
            }
        )
    }
}
private fun openConsultationInWhatsApp(
    context: Context,
    request: LawyerBookingRequest
): Boolean {
    val message = consultationWhatsAppMessage(request)
    val uri = "https://wa.me/$CONSULTATION_WHATSAPP_NUMBER?text=${Uri.encode(message)}".toUri()
    val packages = listOf("com.whatsapp", "com.whatsapp.w4b")

    packages.forEach { packageName ->
        val opened = runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, uri).apply { setPackage(packageName) }
            )
        }.isSuccess
        if (opened) return true
    }

    return runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }.isSuccess
}
internal fun consultationWhatsAppMessage(request: LawyerBookingRequest): String {
    return buildString {
        appendLine("📋 নতুন আইনজীবী পরামর্শের অনুরোধ")
        appendLine("BD Tax Calculator")
        appendLine()
        appendLine("👤 নাম: ${request.clientName}")
        appendLine("📱 মোবাইল: ${request.mobileNumber}")
        if (request.clientEmail.isNotBlank()) {
            appendLine("📧 ইমেইল: ${request.clientEmail}")
        }
        appendLine("⚖️ সেবার ধরন: ${request.category.banglaLabel}")
        appendLine("📞 পরামর্শের মাধ্যম: ${request.mode.banglaLabel}")
        appendLine("📅 পছন্দের তারিখ: ${request.preferredDate}")
        appendLine("⏰ পছন্দের সময়: ${request.preferredTime}")
        if (request.note.isNotBlank()) {
            appendLine()
            appendLine("📝 সমস্যার বিবরণ:")
            append(request.note)
        }
    }.trim()
}
@Composable
private fun <T> ChoiceSection(
    title: String,
    options: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    label: @Composable (T) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = HomeTextPrimary, fontWeight = FontWeight.Bold, fontFamily = TiroBanglaFontFamily)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelected(option) },
                    label = {
                        Text(
                            label(option),
                            fontFamily = TiroBanglaFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CalculatorSurfaceAlt,
                        labelColor = CalculatorFieldText,
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = option == selected,
                        borderColor = CalculatorBorder,
                        selectedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}
@Composable
private fun ConsultationPickerField(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    placeholder: String,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = if (isError) CalculatorDanger else CalculatorFieldText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
        Surface(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CalculatorSurfaceAlt,
            border = BorderStroke(1.dp, if (isError) CalculatorDanger else CalculatorBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value.ifBlank { placeholder },
                    modifier = Modifier.weight(1f),
                    color = when {
                        !enabled -> CalculatorMuted.copy(alpha = 0.55f)
                        value.isBlank() -> CalculatorMuted
                        else -> MaterialTheme.colorScheme.onBackground
                    },
                    fontSize = 13.sp,
                    maxLines = 1,
                    fontFamily = TiroBanglaFontFamily
                )
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(19.dp),
                    tint = if (enabled) MaterialTheme.colorScheme.primary else CalculatorMuted.copy(alpha = 0.55f)
                )
            }
        }
        if (!supportingText.isNullOrBlank()) {
            Text(
                text = supportingText,
                color = if (isError) CalculatorDanger else CalculatorMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConsultationDatePickerDialog(
    currentValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val minimumDateMillis = remember { todayUtcStartMillis() }
    val selectableDates = remember(minimumDateMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minimumDateMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    }
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = parseConsultationDate(currentValue),
        selectableDates = selectableDates
    )
    val pickerColors = DatePickerDefaults.colors(
        containerColor = CalculatorPanel,
        titleContentColor = HomeTextPrimary,
        headlineContentColor = HomeTextPrimary,
        weekdayContentColor = CalculatorMuted,
        subheadContentColor = HomeTextPrimary,
        navigationContentColor = MaterialTheme.colorScheme.primary,
        yearContentColor = HomeTextPrimary,
        disabledYearContentColor = CalculatorMuted.copy(alpha = 0.35f),
        currentYearContentColor = MaterialTheme.colorScheme.primary,
        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
        disabledSelectedYearContentColor = CalculatorMuted.copy(alpha = 0.35f),
        selectedYearContainerColor = MaterialTheme.colorScheme.primary,
        disabledSelectedYearContainerColor = CalculatorSurfaceAlt,
        dayContentColor = HomeTextPrimary,
        disabledDayContentColor = CalculatorMuted.copy(alpha = 0.30f),
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
        disabledSelectedDayContentColor = CalculatorMuted.copy(alpha = 0.35f),
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,
        disabledSelectedDayContainerColor = CalculatorSurfaceAlt,
        todayContentColor = MaterialTheme.colorScheme.primary,
        todayDateBorderColor = MaterialTheme.colorScheme.primary,
        dividerColor = CalculatorBorder
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp,
        colors = pickerColors,
        confirmButton = {
            TextButton(
                enabled = pickerState.selectedDateMillis != null,
                onClick = {
                    pickerState.selectedDateMillis?.let { onConfirm(formatConsultationDate(it)) }
                }
            ) {
                Text(
                    localizedText("নির্বাচন করুন", "Select"),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    localizedText("বাতিল", "Cancel"),
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    ) {
        Column {
            Text(
                localizedText("তারিখ নির্বাচন করুন", "Select date"),
                modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 4.dp),
                color = HomeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontFamily = TiroBanglaFontFamily
            )
            DatePicker(
                state = pickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                colors = pickerColors
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConsultationTimePickerDialog(
    currentValue: String,
    restrictToFutureTime: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialTime = remember(currentValue, restrictToFutureTime) {
        parseConsultationTime(currentValue).takeUnless { (hour, minute) ->
            restrictToFutureTime && !isTimeAfterNow(hour, minute)
        } ?: nextAvailableConsultationTime()
    }
    val pickerState = rememberTimePickerState(
        initialHour = initialTime.first,
        initialMinute = initialTime.second,
        is24Hour = false
    )
    val isValidTime = !restrictToFutureTime || isTimeAfterNow(pickerState.hour, pickerState.minute)
    val pickerColors = TimePickerDefaults.colors(
        clockDialColor = CalculatorSurfaceAlt,
        clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        clockDialUnselectedContentColor = HomeTextPrimary,
        selectorColor = MaterialTheme.colorScheme.primary,
        containerColor = CalculatorPanel,
        periodSelectorBorderColor = CalculatorBorder,
        periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        periodSelectorUnselectedContainerColor = CalculatorSurfaceAlt,
        periodSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        periodSelectorUnselectedContentColor = HomeTextPrimary,
        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.primary,
        timeSelectorUnselectedContainerColor = CalculatorSurfaceAlt,
        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
        timeSelectorUnselectedContentColor = HomeTextPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CalculatorPanel,
        title = {
            Text(
                localizedText("সময় নির্বাচন করুন", "Select time"),
                color = HomeTextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontFamily = TiroBanglaFontFamily
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TimeInput(state = pickerState, colors = pickerColors)
                if (!isValidTime) {
                    Text(
                        localizedText(
                            "আজকের জন্য বর্তমান সময়ের পরের সময় নির্বাচন করুন",
                            "Select a time later than the current time"
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        color = CalculatorDanger,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = TiroBanglaFontFamily
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValidTime,
                onClick = {
                    onConfirm(formatConsultationTime(pickerState.hour, pickerState.minute))
                }
            ) {
                Text(
                    localizedText("নির্বাচন করুন", "Select"),
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    localizedText("বাতিল", "Cancel"),
                    color = CalculatorMuted,
                    fontFamily = TiroBanglaFontFamily
                )
            }
        }
    )
}
private fun parseConsultationDate(value: String): Long? {
    if (value.isBlank()) return null
    return runCatching {
        consultationDateFormatter().parse(value)?.time
    }.getOrNull()
}
private fun formatConsultationDate(timeInMillis: Long): String {
    return consultationDateFormatter().format(Date(timeInMillis))
}
private fun consultationDateFormatter(): SimpleDateFormat {
    return SimpleDateFormat("dd-MM-yyyy", Locale.US).apply {
        isLenient = false
        timeZone = TimeZone.getTimeZone("UTC")
    }
}
private fun parseConsultationTime(value: String): Pair<Int, Int> {
    val fallback = Calendar.getInstance()
    if (value.isBlank()) return fallback.get(Calendar.HOUR_OF_DAY) to fallback.get(Calendar.MINUTE)

    return runCatching {
        val parsed = SimpleDateFormat("h:mm a", Locale.US).apply { isLenient = false }.parse(value)
            ?: return@runCatching fallback.get(Calendar.HOUR_OF_DAY) to fallback.get(Calendar.MINUTE)
        Calendar.getInstance().apply { time = parsed }.let {
            it.get(Calendar.HOUR_OF_DAY) to it.get(Calendar.MINUTE)
        }
    }.getOrDefault(fallback.get(Calendar.HOUR_OF_DAY) to fallback.get(Calendar.MINUTE))
}
private fun formatConsultationTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when (val hourInPeriod = hour % 12) {
        0 -> 12
        else -> hourInPeriod
    }
    return String.format(Locale.US, "%d:%02d %s", displayHour, minute, period)
}
private fun todayUtcStartMillis(): Long {
    val localToday = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(
            localToday.get(Calendar.YEAR),
            localToday.get(Calendar.MONTH),
            localToday.get(Calendar.DAY_OF_MONTH),
            0,
            0,
            0
        )
    }.timeInMillis
}
private fun currentConsultationDate(): String {
    return SimpleDateFormat("dd-MM-yyyy", Locale.US).format(Date())
}
private fun isTimeAfterNow(hour: Int, minute: Int): Boolean {
    val now = Calendar.getInstance()
    val selectedMinutes = hour * 60 + minute
    val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    return selectedMinutes > currentMinutes
}
private fun nextAvailableConsultationTime(): Pair<Int, Int> {
    val now = Calendar.getInstance()
    val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    val nextQuarterHour = ((currentMinutes + 15) / 15) * 15
    val safeMinutes = nextQuarterHour.coerceAtMost((24 * 60) - 1)
    return safeMinutes / 60 to safeMinutes % 60
}
internal fun isConsultationScheduleInFuture(
    date: String,
    time: String,
    nowMillis: Long = System.currentTimeMillis()
): Boolean {
    if (date.isBlank() || time.isBlank()) return false
    return runCatching {
        val selected = SimpleDateFormat("dd-MM-yyyy h:mm a", Locale.US).apply {
            isLenient = false
        }.parse("$date $time") ?: return@runCatching false
        selected.time > nowMillis
    }.getOrDefault(false)
}

@Composable
private fun ConsultationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    supportingText: String? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = if (isError) CalculatorDanger else CalculatorFieldText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = TiroBanglaFontFamily
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = CalculatorSurfaceAlt,
            border = BorderStroke(1.dp, if (isError) CalculatorDanger else CalculatorBorder)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = TiroBanglaFontFamily,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                minLines = minLines,
                maxLines = if (singleLine) 1 else 5,
                decorationBox = { innerTextField ->
                    androidx.compose.foundation.layout.Box {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                placeholder,
                                color = CalculatorMuted,
                                fontSize = 14.sp,
                                fontFamily = TiroBanglaFontFamily
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
        if (!supportingText.isNullOrBlank()) {
            Text(
                supportingText,
                color = if (isError) CalculatorDanger else CalculatorMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                fontFamily = TiroBanglaFontFamily
            )
        }
    }
}
@Composable
private fun BookingRequests(
    requests: List<LawyerBookingRequest>,
    onCreate: () -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 18.dp, 16.dp, FloatingBottomBarSafePadding),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = CalculatorHeroMiddle), shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(localizedText("নতুন অনুরোধ", "New request"), fontFamily = TiroBanglaFontFamily)
            }
        }
        if (requests.isEmpty()) {
            item {
                Text(localizedText("এখনো কোনো অনুরোধ নেই।", "No requests yet."), modifier = Modifier.padding(vertical = 28.dp), color = CalculatorMuted, fontFamily = TiroBanglaFontFamily)
            }
        } else {
            items(requests, key = LawyerBookingRequest::id) { request ->
                BookingRequestCard(request, onDelete)
            }
        }
    }
}
@Composable
private fun BookingRequestCard(request: LawyerBookingRequest, onDelete: (String) -> Unit) {
    val uriHandler = LocalUriHandler.current
    val canJoinMeet = request.status == BookingStatus.Confirmed && isSafeGoogleMeetUrl(request.meetingUrl)
    Card(colors = CardDefaults.cardColors(containerColor = CalculatorPanel), border = BorderStroke(1.dp, CalculatorBorder), shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(localizedText(request.category.banglaLabel, request.category.englishLabel), color = HomeTextPrimary, fontWeight = FontWeight.ExtraBold, fontFamily = TiroBanglaFontFamily)
                Surface(color = CalculatorAccentSoft, shape = RoundedCornerShape(999.dp)) {
                    Text(localizedText(request.status.banglaLabel, request.status.englishLabel), modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), color = CalculatorSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = TiroBanglaFontFamily)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = CalculatorMuted, modifier = Modifier.size(17.dp))
                Text("${request.preferredDate} · ${request.preferredTime}", color = CalculatorMuted, fontSize = 12.sp)
            }
            Text(localizedText(request.mode.banglaLabel, request.mode.englishLabel), color = CalculatorMuted, fontSize = 12.sp, fontFamily = TiroBanglaFontFamily)
            if (request.lawyerName != null) {
                Text(request.lawyerName, color = HomeTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = TiroBanglaFontFamily)
            }
            if (canJoinMeet) {
                Button(
                    onClick = {
                        request.meetingUrl?.let {
                            FirebaseTracker.logEvent("lawyer_meet_join_clicked")
                            uriHandler.openUri(it)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CalculatorHeroMiddle),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.VideoCall, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text(localizedText("Google Meet-এ যোগ দিন", "Join Google Meet"), fontFamily = TiroBanglaFontFamily)
                }
            }
            if (request.status == BookingStatus.Draft) {
                IconButton(onClick = { onDelete(request.id) }, modifier = Modifier.align(Alignment.End)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = localizedText("খসড়া মুছুন", "Delete draft"), tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
private fun isSafeGoogleMeetUrl(value: String?): Boolean {
    if (value.isNullOrBlank()) return false
    return runCatching {
        val uri = value.toUri()
        uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals("meet.google.com", ignoreCase = true) &&
            !uri.path.isNullOrBlank()
    }.getOrDefault(false)
}
