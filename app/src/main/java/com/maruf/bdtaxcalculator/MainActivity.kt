// MainActivity.kt
package com.maruf.bdtaxcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BDTaxCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaxCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun BDTaxCalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2E7D32),
            secondary = Color(0xFF00897B),
            tertiary = Color(0xFF4CAF50),
            background = Color(0xFFF1F8E9),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color(0xFF1B5E20),
            onSurface = Color(0xFF1B5E20)
        ),
        content = content
    )
}

data class TaxpayerType(
    val id: String,
    val label: String,
    val taxFreeLimit: Long
)

data class TaxBreakdown(
    val label: String,
    val amount: Long,
    val rate: Double,
    val tax: Double
)

// Update TaxResult data class to include rebate information
data class TaxResult(
    val totalTax: Double,
    val breakdown: List<TaxBreakdown>,
    val taxableAmount: Long,
    val isMinimumTax: Boolean,
    val investmentRebate: Double = 0.0, // Add this
    val taxAfterRebate: Double = 0.0    // Add this
)

data class InvestmentInputData(
    val type: String,
    val title: String,
    val allowablePercentage: Double,
    val taxRate: Double,
    val maxLimit: Long,
    var amount: String = ""
)

data class SalaryBreakdown(
    val grossSalary: Long,
    val basicSalary: Long,
    val houseRent: Long,
    val medical: Long,
    val conveyance: Long,
    val otherAllowances: Long,
    val yearlyBonus: Long,
    val totalIncome: Long,
    val totalExemption: Long,
    val taxableIncome: Long
)

enum class CalculatorMode {
    TAXABLE_INCOME, GROSS_SALARY
}

// Update the main TaxCalculatorScreen to use Double for investmentRebate
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxCalculatorScreen() {
    var mode by remember { mutableStateOf(CalculatorMode.TAXABLE_INCOME) }
    var income by remember { mutableStateOf("") }
    var grossSalary by remember { mutableStateOf("") }
    var yearlyBonus by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("general") }
    var showInfo by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val taxpayerTypes = listOf(
        TaxpayerType("general", "সাধারণ করদাতা", 375000),
        TaxpayerType("women", "মহিলা করদাতা", 400000),
        TaxpayerType("senior", "সিনিয়র সিটিজেন (৬৫+)", 400000),
        TaxpayerType("disabled", "প্রতিবন্ধী ব্যক্তি", 475000),
        TaxpayerType("freedomFighter", "মুক্তিযোদ্ধা", 500000)
    )

    val currentType = taxpayerTypes.find { it.id == selectedType }!!

    val salaryBreakdown = if (mode == CalculatorMode.GROSS_SALARY) {
        calculateSalaryBreakdown(
            grossSalary = grossSalary.toLongOrNull() ?: 0L,
            yearlyBonus = yearlyBonus.toLongOrNull() ?: 0L
        )
    } else null

    val incomeValue = if (mode == CalculatorMode.GROSS_SALARY) {
        salaryBreakdown?.taxableIncome ?: 0L
    } else {
        income.toLongOrNull() ?: 0L
    }

    var investments by remember {
        mutableStateOf(
            listOf(
                InvestmentInputData("dse", "DSE শেয়ার", 0.15, 0.15, 5_000_000),
                InvestmentInputData("sanchaypatra", "সঞ্চয়পত্র", 0.10, 0.10, 7_500_000),
                InvestmentInputData("dps", "DPS (ডিপোজিট পেনশন স্কিম)", 0.15, 0.15, 15_000_000),
                InvestmentInputData("mutual", "মিউচুয়াল ফান্ড", 0.15, 0.15, 5_000_000),
                InvestmentInputData("insurance", "লাইফ ইন্স্যুরেন্স", 0.10, 0.10, 12_000_000)
            )
        )
    }

    // Calculate investment rebate as Double
    val investmentRebate = calculateInvestmentRebate(investments, incomeValue)

    // Calculate tax with investment rebate
    val result = calculateTax(incomeValue, currentType.taxFreeLimit, investmentRebate)

    Scaffold(
        topBar = {
            // ... rest of the Scaffold code remains the same
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "আয়কর ক্যালকুলেটর",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "২০২৫-২৬",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { showInfo = !showInfo }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "তথ্য",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF1F8E9),
                            Color(0xFFE8F5E9)
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = showInfo,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                InfoCard()
            }

            ModeSelectionCard(
                selectedMode = mode,
                onModeChange = { mode = it }
            )

            TaxpayerTypeSection(
                taxpayerTypes = taxpayerTypes,
                selectedType = selectedType,
                onTypeSelect = { selectedType = it }
            )

            if (mode == CalculatorMode.TAXABLE_INCOME) {
                TaxableIncomeInput(
                    income = income,
                    onIncomeChange = { income = it },
                    taxFreeLimit = currentType.taxFreeLimit
                )
            } else {
                GrossSalaryInput(
                    grossSalary = grossSalary,
                    yearlyBonus = yearlyBonus,
                    onGrossSalaryChange = { grossSalary = it },
                    onYearlyBonusChange = { yearlyBonus = it }
                )

                if (salaryBreakdown != null) {
                    SalaryBreakdownCard(salaryBreakdown)
                }
            }

            if (incomeValue > currentType.taxFreeLimit) {
                RecommendedInvestmentCard(
                    taxableIncome = incomeValue,
                    currentRebate = investmentRebate
                )
            }
            InvestmentInputSection(
                investments = investments,
                onInvestmentChange = { type, value ->
                    investments = investments.map {
                        if (it.type == type) it.copy(amount = value) else it
                    }
                },
                taxableIncome = incomeValue
            )



            if (incomeValue <= currentType.taxFreeLimit) {
                TaxFreeCard(income = incomeValue, limit = currentType.taxFreeLimit)
            } else {
                TaxResultCard(
                    result = result,
                    taxFreeLimit = currentType.taxFreeLimit,
                    investmentRebate = investmentRebate
                )
                TaxBreakdownCard(
                    result = result,
                    investmentRebate = investmentRebate
                )
            }

            TaxSlabsCard()

            Text(
                "বাংলাদেশ জাতীয় রাজস্ব বোর্ড (NBR) অনুযায়ী",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ModeSelectionCard(
    selectedMode: CalculatorMode,
    onModeChange: (CalculatorMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "হিসাবের ধরন নির্বাচন করুন",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModeButton(
                    text = "করযোগ্য আয়",
                    icon = Icons.Default.Calculate,
                    isSelected = selectedMode == CalculatorMode.TAXABLE_INCOME,
                    onClick = { onModeChange(CalculatorMode.TAXABLE_INCOME) },
                    modifier = Modifier.weight(1f)
                )

                ModeButton(
                    text = "মোট বেতন",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedMode == CalculatorMode.GROSS_SALARY,
                    onClick = { onModeChange(CalculatorMode.GROSS_SALARY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ModeButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF2E7D32) else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color(0xFF1B5E20) else Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GrossSalaryInput(
    grossSalary: String,
    yearlyBonus: String,
    onGrossSalaryChange: (String) -> Unit,
    onYearlyBonusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    "মাসিক মোট বেতন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = grossSalary,
                onValueChange = { onGrossSalaryChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("মাসিক মোট বেতন (টাকা)") },
                placeholder = { Text("যেমন: 5000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = {
                    Text("৳", fontSize = 20.sp, color = Color(0xFF2E7D32))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = yearlyBonus,
                onValueChange = { onYearlyBonusChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("বার্ষিক মোট বোনাস (টাকা)") },
                placeholder = { Text("যেমন: 10000") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "💡 সাধারণত বেসিক বেতন মোট বেতনের ৫০-৬০%",
                fontSize = 12.sp,
                color = Color(0xFF558B2F),
                fontStyle = FontStyle.Italic
            )
        }
    }
}

// Updated InvestmentInputSection to show breakdown
@Composable
fun InvestmentInputSection(
    investments: List<InvestmentInputData>,
    onInvestmentChange: (String, String) -> Unit,
    taxableIncome: Long
) {
    val totalInvestment = investments.sumOf { inv ->
        inv.amount.toLongOrNull() ?: 0L
    }

    val threePercentOfTaxable = taxableIncome * 0.03
    val fifteenPercentOfInvestment = totalInvestment * 0.15
    val fixedCap = 1_000_000.0

    val rebate = calculateInvestmentRebate(investments, taxableIncome)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "বিনিয়োগের তথ্য (Investment Rebate)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(Modifier.height(12.dp))

            investments.forEach { inv ->
                OutlinedTextField(
                    value = inv.amount,
                    onValueChange = { newValue ->
                        onInvestmentChange(inv.type, newValue.filter { it.isDigit() })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(inv.title) },
                    placeholder = { Text("যেমন: 500000") },
                    leadingIcon = { Text("৳", color = Color(0xFF2E7D32)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2E7D32),
                        focusedLabelColor = Color(0xFF2E7D32)
                    )
                )
                Spacer(Modifier.height(8.dp))
            }

            if (totalInvestment > 0) {
                Divider(color = Color(0xFF81C784), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))

                // Show calculation breakdown
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "রিবেট হিসাব:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("৩% করযোগ্য আয়ের:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "৳${formatBengaliNumber(threePercentOfTaxable.toLong())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("১৫% বিনিয়োগের:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "৳${formatBengaliNumber(fifteenPercentOfInvestment.toLong())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সর্বোচ্চ সীমা:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                "৳${formatBengaliNumber(fixedCap.toLong())}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        Divider(color = Color(0xFF4CAF50), thickness = 1.dp)
                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "মোট ট্যাক্স রিবেট (সর্বনিম্ন):",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                fontSize = 14.sp
                            )
                            Text(
                                "৳${formatBengaliNumber(rebate.toLong())}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ✅ Enhanced SalaryBreakdownCard to show exemption details
@Composable
fun SalaryBreakdownCard(breakdown: SalaryBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Assessment,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    "বেতনের বিস্তারিত হিসাব",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Monthly Breakdown
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF1F8E9)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "মাসিক বেতন বিভাজন",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    SalaryItem("মূল বেতন (৫০%)", breakdown.basicSalary)
                    SalaryItem("বাড়ি ভাড়া (বেসিকের ৫০%)", breakdown.houseRent, isExempt = true)
                    SalaryItem("চিকিৎসা (বেসিকের ১০%)", breakdown.medical, isExempt = true)
                    SalaryItem("যাতায়াত (৫%)", breakdown.conveyance, isExempt = true)
                    SalaryItem("অন্যান্য ভাতা", breakdown.otherAllowances)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness,
                        color = DividerDefaults.color
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "মোট মাসিক:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            "${formatBengaliNumber(breakdown.grossSalary)} ৳",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Yearly Summary with Exemption Details
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "বার্ষিক সারসংক্ষেপ",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    YearlyItem("মাসিক বেতন × ১২", breakdown.grossSalary * 12)
                    YearlyItem("বার্ষিক বোনাস", breakdown.yearlyBonus, highlight = true)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness,
                        color = Color(0xFF1976D2)
                    )

                    YearlyItem("মোট বার্ষিক আয়", breakdown.totalIncome, isBold = true)

                    // Show exemption calculation
                    val yearlyHouseRent = breakdown.houseRent * 12
                    val yearlyMedical = breakdown.medical * 12
                    val yearlyConveyance = breakdown.conveyance * 12
                    val oneThirdIncome = breakdown.totalIncome / 3

                    Spacer(Modifier.height(8.dp))
                    Text(
                        "ছাড়ের হিসাব:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• বাড়ি ভাড়া (≤৩ লক্ষ):", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            formatBengaliNumber(minOf(yearlyHouseRent, 300000L)),
                            fontSize = 11.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• চিকিৎসা (≤১.২ লক্ষ):", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            formatBengaliNumber(minOf(yearlyMedical, 120000L)),
                            fontSize = 11.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• যাতায়াত (≤৩০ হাজার):", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            formatBengaliNumber(minOf(yearlyConveyance, 30000L)),
                            fontSize = 11.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• আয়ের ১/৩:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            formatBengaliNumber(oneThirdIncome),
                            fontSize = 11.sp,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• সর্বোচ্চ সীমা:", fontSize = 11.sp, color = Color.Gray)
                        Text(
                            "৫,০০,০০০",
                            fontSize = 11.sp,
                            color = Color(0xFF1565C0)
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    YearlyItem("(-) মোট ছাড় (সর্বনিম্ন)", breakdown.totalExemption, isNegative = true)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = DividerDefaults.Thickness,
                        color = Color(0xFF1976D2)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "করযোগ্য আয়:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0D47A1)
                        )
                        Text(
                            "${formatBengaliNumber(breakdown.taxableIncome)} ৳",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1565C0)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SalaryItem(label: String, amount: Long, isExempt: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                fontSize = 13.sp,
                color = Color(0xFF33691E)
            )
            if (isExempt) {
                Text(
                    "✓",
                    fontSize = 10.sp,
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Text(
            "${formatBengaliNumber(amount)} ৳",
            fontSize = 13.sp,
            color = Color(0xFF33691E)
        )
    }
}

@Composable
fun YearlyItem(
    label: String,
    amount: Long,
    isBold: Boolean = false,
    isNegative: Boolean = false,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = if (isBold) 14.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) Color(0xFFFF6F00) else Color(0xFF1565C0)
        )
        Text(
            "${if (isNegative) "-" else ""}${formatBengaliNumber(amount)} ৳",
            fontSize = if (isBold) 14.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isNegative) Color(0xFFD32F2F) else if (highlight) Color(0xFFFF6F00) else Color(0xFF1565C0)
        )
    }
}

// Updated InfoCard with correct information
// ✅ Updated InfoCard with correct percentages
@Composable
fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF1976D2)
                )
                Text(
                    "গুরুত্বপূর্ণ তথ্য",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "📊 বেতন বিভাজন:\n" +
                        "• বেসিক = মোট বেতনের ৫০%\n" +
                        "• বাড়ি ভাড়া = বেসিকের ৫০%\n" +
                        "• চিকিৎসা = বেসিকের ১০%\n" +
                        "• যাতায়াত = মোট বেতনের ৫%\n\n" +
                        "💰 ছাড়ের নিয়ম:\n" +
                        "• বাড়ি ভাড়া: সর্বোচ্চ ৩,০০,০০০ টাকা\n" +
                        "• চিকিৎসা: সর্বোচ্চ ১,২০,০০০ টাকা\n" +
                        "• যাতায়াত: সর্বোচ্চ ৩০,০০০ টাকা\n" +
                        "• মোট ছাড় = সর্বনিম্ন (উপরের যোগফল, আয়ের ১/৩, ৫,০০,০০০)\n\n" +
                        "🎯 বিনিয়োগ রিবেট:\n" +
                        "• সর্বনিম্ন (করযোগ্য আয়ের ৩%, বিনিয়োগের ১৫%, ১০ লক্ষ)\n\n" +
                        "📅 এই ক্যালকুলেটর ২০২৫-২৬ অর্থবছরের কর স্ল্যাব অনুযায়ী",
                fontSize = 11.sp,
                color = Color(0xFF1565C0),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun TaxpayerTypeSection(
    taxpayerTypes: List<TaxpayerType>,
    selectedType: String,
    onTypeSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    "করদাতার ধরন",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            taxpayerTypes.forEach { type ->
                TaxpayerTypeCard(
                    type = type,
                    isSelected = selectedType == type.id,
                    onSelect = { onTypeSelect(type.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TaxableIncomeInput(
    income: String,
    onIncomeChange: (String) -> Unit,
    taxFreeLimit: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    "করযোগ্য আয়",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = income,
                onValueChange = { onIncomeChange(it.filter { c -> c.isDigit() }) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("আয়ের পরিমাণ (টাকা)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    focusedLabelColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "করমুক্ত সীমা: ${formatBengaliNumber(taxFreeLimit)} ৳",
                fontSize = 14.sp,
                color = Color(0xFF558B2F)
            )
        }
    }
}

@Composable
fun TaxpayerTypeCard(
    type: TaxpayerType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFF2E7D32),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    type.label,
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color(0xFF1B5E20) else Color(0xFF424242)
                )
                Text(
                    "করমুক্ত: ${formatBengaliNumber(type.taxFreeLimit)} ৳",
                    fontSize = 12.sp,
                    color = if (isSelected) Color(0xFF2E7D32) else Color.Gray
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "নির্বাচিত",
                    tint = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun TaxFreeCard(income: Long, limit: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF1976D2)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "আপনার আয় করমুক্ত সীমার মধ্যে",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "আপনার আয় ${formatBengaliNumber(income)} ৳ যা করমুক্ত সীমা ${formatBengaliNumber(limit)} ৳ এর মধ্যে।",
                fontSize = 14.sp,
                color = Color(0xFF1976D2),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "আপনাকে কোন আয়কর দিতে হবে না।",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0),
                textAlign = TextAlign.Center
            )
        }
    }
}


// Update the TaxResultCard composable to handle Double values properly
@Composable
fun TaxResultCard(result: TaxResult, taxFreeLimit: Long, investmentRebate: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2E7D32),
                            Color(0xFF388E3C)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "মোট আয়কর",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${formatBengaliNumber(result.taxAfterRebate.toLong())} ৳",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "(${NumberFormat.getNumberInstance(Locale.US).format(result.taxAfterRebate.toLong())} BDT)",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                // Show rebate information if applicable
                if (investmentRebate > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "বিনিয়োগ রিবেট: ${formatBengaliNumber(investmentRebate.toLong())} ৳",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                if (result.isMinimumTax) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            "ন্যূনতম কর: ৫,০০০ টাকা প্রযোজ্য",
                            modifier = Modifier.padding(8.dp),
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFF9C4)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("করমুক্ত সীমা:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${formatBengaliNumber(taxFreeLimit)} ৳", fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("করযোগ্য আয়:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${formatBengaliNumber(result.taxableAmount)} ৳", fontSize = 13.sp)
            }
            if (investmentRebate > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("বিনিয়োগ রিবেট:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${formatBengaliNumber(investmentRebate.toLong())} ৳", fontSize = 13.sp, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

// Update the TaxBreakdownCard composable
@Composable
fun TaxBreakdownCard(result: TaxResult, investmentRebate: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
                Text(
                    "কর হিসাবের বিস্তারিত",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            result.breakdown.forEach { item ->
                BreakdownItem(item)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Show rebate deduction if applicable
            if (investmentRebate > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF4CAF50))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "বিনিয়োগ রিবেট কাটা:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            "- ${formatBengaliNumber(investmentRebate.toLong())} ৳",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 2.dp,
                color = Color(0xFF2E7D32)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "মোট আয়কর:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    "${formatBengaliNumber(result.taxAfterRebate.toLong())} ৳",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
fun BreakdownItem(item: TaxBreakdown) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F8E9)
        ),
        border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF33691E),
                    modifier = Modifier.weight(1f)
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFC8E6C9)
                    )
                ) {
                    Text(
                        "${item.rate.toInt()}% কর",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("করযোগ্য অংশ:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "${formatBengaliNumber(item.amount)} ৳",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF33691E)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("কর:", fontSize = 11.sp, color = Color.Gray)
                    Text(
                        "${formatBengaliNumber(item.tax.toLong())} ৳",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

// ✅ NEW: Recommended Investment Card
@Composable
fun RecommendedInvestmentCard(taxableIncome: Long, currentRebate: Double) {
    // Calculate optimal investment for maximum rebate
    val threePercentOfTaxable = taxableIncome * 0.03
    val maxRebatePossible = minOf(threePercentOfTaxable, 1_000_000.0)

    // To get max rebate: rebate = investment × 0.15
    // So: investment = rebate / 0.15
    val optimalInvestment = (maxRebatePossible / 0.15).toLong()

    // Calculate potential tax savings
    val potentialSavings = maxRebatePossible - currentRebate

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1565C0),
                            Color(0xFF0D47A1)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "প্রস্তাবিত বিনিয়োগ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Optimal Investment Amount
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "সর্বোচ্চ ছাড়ের জন্য বিনিয়োগ করুন:",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "৳${formatBengaliNumber(optimalInvestment)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFEB3B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Max Rebate Possible
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "সর্বোচ্চ সম্ভাব্য ট্যাক্স রিবেট:",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                "৳${formatBengaliNumber(maxRebatePossible.toLong())}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                // Investment Options
                Text(
                    "💡 বিনিয়োগের বিকল্প:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                val investmentOptions = listOf(
                    "DSE শেয়ার - ১৫% রিবেট",
                    "সঞ্চয়পত্র - ১০% রিবেট",
                    "DPS - ১৫% রিবেট",
                    "মিউচুয়াল ফান্ড - ১৫% রিবেট",
                    "লাইফ ইন্স্যুরেন্স - ১০% রিবেট"
                )

                investmentOptions.forEach { option ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("•", color = Color(0xFF4CAF50), fontSize = 16.sp)
                        Text(
                            option,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun TaxSlabsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2E7D32),
                            Color(0xFF00897B)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        "কর স্ল্যাব ২০২৫-২৬",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val slabs = listOf(
                    "(ক) প্রথম ৩,৭৫,০০০ টাকা পর্যন্ত" to "শূন্য",
                    "(খ) পরবর্তী ৩,০০,০০০ টাকা" to "১০%",
                    "(গ) পরবর্তী ৪,০০,০০০ টাকা" to "১৫%",
                    "(ঘ) পরবর্তী ৫,০০,০০০ টাকা" to "২০%",
                    "(ঙ) পরবর্তী ২০,০০,০০০ টাকা" to "২৫%",
                    "(চ) অবশিষ্ট আয়" to "৩০%"
                )

                slabs.forEach { (label, rate) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                label,
                                fontSize = 13.sp,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                rate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// Updated calculateSalaryBreakdown function with correct formula
// ✅ CORRECTED: calculateSalaryBreakdown function
fun calculateSalaryBreakdown(grossSalary: Long, yearlyBonus: Long): SalaryBreakdown {
    // Basic = 50% of Gross
    val basicSalary = (grossSalary * 0.50).toLong()

    // House Rent = 50% of Basic (NOT 50% of gross)
    val houseRent = (basicSalary * 0.50).toLong()

    // Medical = 10% of Basic (NOT 10% of gross)
    val medical = (basicSalary * 0.10).toLong()

    // Conveyance = 5% of Gross
    val conveyance = (grossSalary * 0.05).toLong()

    // Other allowances = remainder
    val otherAllowances = grossSalary - (basicSalary + houseRent + medical + conveyance)

    // ✅ CORRECTED: Yearly calculations
    val yearlyBasic = basicSalary * 12
    val yearlyHouseRent = houseRent * 12
    val yearlyMedical = medical * 12
    val yearlyConveyance = conveyance * 12
    val yearlyOther = otherAllowances * 12

    // Total Yearly Income = All components × 12 + Bonus
    val totalIncome = yearlyBasic + yearlyHouseRent + yearlyMedical + yearlyConveyance + yearlyOther + yearlyBonus

    // ✅ CORRECTED: Exemptions calculation
    // Individual exemption limits
    val houseRentExemptionLimit = minOf(yearlyHouseRent, 300000L)
    val medicalExemptionLimit = minOf(yearlyMedical, 120000L)
    val conveyanceExemptionLimit = minOf(yearlyConveyance, 30000L)

    // Total of individual exemptions
    val totalIndividualExemptions = houseRentExemptionLimit + medicalExemptionLimit + conveyanceExemptionLimit

    // ✅ Apply 1/3rd rule: Exemption = lower of (total individual exemptions, 1/3 of income, 500,000)
    val oneThirdIncome = totalIncome / 3
    val exemptionMin = minOf(oneThirdIncome, 500000L)
    // TODO: need info from farhan bhai
    //val totalExemption = minOf(totalIndividualExemptions, exemptionCap)

    val taxableIncome = maxOf(0, totalIncome - exemptionMin)

    return SalaryBreakdown(
        grossSalary = grossSalary,
        basicSalary = basicSalary,
        houseRent = houseRent,
        medical = medical,
        conveyance = conveyance,
        otherAllowances = otherAllowances,
        yearlyBonus = yearlyBonus,
        totalIncome = totalIncome,
        totalExemption = exemptionMin,
        taxableIncome = taxableIncome
    )
}

// Update the calculateInvestmentRebate function to return Double
fun calculateInvestmentRebate(investments: List<InvestmentInputData>, taxableIncome: Long): Double {
    // Calculate total investment amount
    val totalInvestment = investments.sumOf { inv ->
        inv.amount.toLongOrNull() ?: 0L
    }

    // Rule: Rebate = Lowest of:
    // 1. 3% of taxable income
    // 2. 15% of actual investment
    // 3. Fixed 10 Lac (1,000,000 BDT)

    val threePercentOfTaxable = taxableIncome * 0.03
    val fifteenPercentOfInvestment = totalInvestment * 0.15
    val fixedCap = 1_000_000.0

    return minOf(threePercentOfTaxable, fifteenPercentOfInvestment, fixedCap)
}


// Update calculateTax function to include investment rebate
fun calculateTax(income: Long, taxFreeLimit: Long, investmentRebate: Double = 0.0): TaxResult {
    if (income <= taxFreeLimit) {
        return TaxResult(0.0, emptyList(), 0, false, investmentRebate, 0.0)
    }

    var tax = 0.0
    val breakdown = mutableListOf<TaxBreakdown>()

    val slabs = listOf(
        Triple(675000L, 0.10, "পরবর্তী ৩,০০,০০০ টাকা (৩,৭৫,০০০ - ৬,৭৫,০০০)"),
        Triple(1075000L, 0.15, "পরবর্তী ৪,০০,০০০ টাকা (৬,৭৫,০০০ - ১০,৭৫,০০০)"),
        Triple(1575000L, 0.20, "পরবর্তী ৫,০০,০০০ টাকা (১০,৭৫,০০০ - ১৫,৭৫,০০০)"),
        Triple(3575000L, 0.25, "পরবর্তী ২০,০০,০০০ টাকা (১৫,৭৫,০০০ - ৩৫,৭৫,০০০)"),
        Triple(Long.MAX_VALUE, 0.30, "অবশিষ্ট আয় (৩৫,৭৫,০০০ এর উপরে)")
    )

    var lowerBound = taxFreeLimit

    for ((upper, rate, label) in slabs) {
        if (income <= lowerBound) break

        val taxableInSlab = minOf(income, upper) - lowerBound

        if (taxableInSlab > 0) {
            val taxInSlab = taxableInSlab * rate

            breakdown.add(
                TaxBreakdown(
                    label = label,
                    amount = taxableInSlab,
                    rate = rate * 100,
                    tax = taxInSlab
                )
            )

            tax += taxInSlab
            lowerBound = upper
        }
    }

    val minimumTax = 5000.0
    val taxBeforeRebate = maxOf(tax, minimumTax)

    // Apply investment rebate
    val taxAfterRebate = maxOf(taxBeforeRebate - investmentRebate, 0.0)

    return TaxResult(
        totalTax = taxAfterRebate, // Now this is the final tax after rebate
        breakdown = breakdown,
        taxableAmount = income - taxFreeLimit,
        isMinimumTax = tax < minimumTax && tax > 0,
        investmentRebate = investmentRebate,
        taxAfterRebate = taxAfterRebate
    )
}

fun formatBengaliNumber(number: Long): String {
    val bengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    val formatted = NumberFormat.getNumberInstance(Locale.US).format(number)
    return formatted.map { char ->
        if (char.isDigit()) bengaliDigits[char.toString().toInt()] else char
    }.joinToString("")
}













