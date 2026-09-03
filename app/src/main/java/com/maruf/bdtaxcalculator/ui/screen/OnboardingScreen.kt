package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maruf.bdtaxcalculator.R
import com.maruf.bdtaxcalculator.ui.localizedText
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientBottom
import com.maruf.bdtaxcalculator.ui.theme.CalculatorGradientTop
import com.maruf.bdtaxcalculator.ui.theme.CalculatorInk
import com.maruf.bdtaxcalculator.ui.theme.CalculatorMuted
import com.maruf.bdtaxcalculator.ui.theme.CalculatorSuccess
import com.maruf.bdtaxcalculator.ui.theme.TiroBanglaFontFamily
import kotlinx.coroutines.launch

private enum class OnboardingIllustration {
    Welcome,
    Calculation,
    Investment
}

@Immutable
private data class OnboardingPageData(
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val actionBn: String,
    val actionEn: String,
    val illustration: OnboardingIllustration
)

@Composable
fun OnboardingScreen(onStartCalculation: () -> Unit) {
    val pages = listOf(
        OnboardingPageData(
            titleBn = "সহজে জানুন আপনার\nআয়কর",
            titleEn = "Understand your\nincome tax easily",
            descriptionBn = "মাত্র কয়েক মিনিটে আপনার আয়কর, রিবেট ও মাসিক করের হিসাব করে নিন।",
            descriptionEn = "Calculate your income tax, rebate and estimated monthly tax in minutes.",
            actionBn = "পরবর্তী সুবিধা",
            actionEn = "Next feature",
            illustration = OnboardingIllustration.Welcome
        ),
        OnboardingPageData(
            titleBn = "দ্রুত ও নির্ভুল\nট্যাক্স ক্যালকুলেশন",
            titleEn = "Fast and clear\ntax calculation",
            descriptionBn = "আপনার আয় ও প্রয়োজনীয় তথ্য দিন, কয়েক সেকেন্ডেই সম্ভাব্য করের হিসাব দেখুন।",
            descriptionEn = "Enter your income and key details to see an estimated tax in seconds.",
            actionBn = "রিবেট সুবিধা দেখুন",
            actionEn = "Explore rebate",
            illustration = OnboardingIllustration.Calculation
        ),
        OnboardingPageData(
            titleBn = "বিনিয়োগ ও রিবেট\nহিসাব সহজে",
            titleEn = "Investment and rebate\nmade simple",
            descriptionBn = "GPF, DPS, লাইফ ইন্স্যুরেন্স, সঞ্চয়পত্র, শেয়ার ও মিউচুয়াল ফান্ডসহ অনুমোদিত বিনিয়োগের রিবেট হিসাব করুন।",
            descriptionEn = "Calculate rebate for GPF, DPS, life insurance, savings certificates, shares and mutual funds.",
            actionBn = "হিসাব শুরু করুন",
            actionEn = "Start calculating",
            illustration = OnboardingIllustration.Investment
        )
    )
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(CalculatorGradientTop, CalculatorGradientBottom)
                )
            )
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { pageIndex ->
            OnboardingPage(page = pages[pageIndex])
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 22.dp, end = 22.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Button(
                onClick = {
                    if (currentPage == pages.lastIndex) {
                        onStartCalculation()
                    } else {
                        scope.launch { pagerState.animateScrollToPage(currentPage + 1) }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        localizedText(pages[currentPage].actionBn, pages[currentPage].actionEn),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = TiroBanglaFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterEnd).size(20.dp)
                    )
                }
            }
            PagerIndicator(pageCount = pages.size, currentPage = currentPage)
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPageData) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        val compact = maxHeight < 680.dp
        val illustrationHeight = when {
            compact -> 178.dp
            maxHeight < 820.dp -> 236.dp
            else -> 258.dp
        }
        val topPadding = when {
            compact -> 26.dp
            maxHeight < 820.dp -> 56.dp
            else -> 76.dp
        }
        val illustrationToTitleSpace = when {
            compact -> 22.dp
            maxHeight < 820.dp -> 54.dp
            else -> 68.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = if (compact) 126.dp else 146.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingIllustration(type = page.illustration, height = illustrationHeight)
            Spacer(Modifier.height(illustrationToTitleSpace))
            Text(
                text = localizedText(page.titleBn, page.titleEn),
                color = CalculatorInk,
                fontSize = if (compact) 23.sp else 27.sp,
                lineHeight = if (compact) 31.sp else 35.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = TiroBanglaFontFamily,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(if (compact) 8.dp else 12.dp))
            Text(
                text = localizedText(page.descriptionBn, page.descriptionEn),
                modifier = Modifier.fillMaxWidth(0.94f),
                color = CalculatorMuted,
                fontSize = if (compact) 13.sp else 14.sp,
                lineHeight = if (compact) 20.sp else 22.sp,
                fontFamily = TiroBanglaFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OnboardingIllustration(type: OnboardingIllustration, height: Dp) {
    Box(
        modifier = Modifier.fillMaxWidth().height(height),
        contentAlignment = Alignment.Center
    ) {
        when (type) {
            OnboardingIllustration.Welcome -> WelcomeIllustration()
            OnboardingIllustration.Calculation -> CalculationIllustration()
            OnboardingIllustration.Investment -> InvestmentIllustration()
        }
    }
}

@Composable
private fun WelcomeIllustration() {
    Image(
        painter = painterResource(R.drawable.onboarding_welcome_tax),
        contentDescription = localizedText(
            "ট্যাক্স ক্যালকুলেটর ও আয়কর হিসাবের চিত্র",
            "Tax calculator and income tax illustration"
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun CalculationIllustration() {
    Image(
        painter = painterResource(R.drawable.onboarding_calculation_tax),
        contentDescription = localizedText(
            "যাচাইকৃত আয়কর হিসাবের চিত্র",
            "Verified income tax calculation illustration"
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun InvestmentIllustration() {
    Image(
        painter = painterResource(R.drawable.onboarding_investment_rebate),
        contentDescription = localizedText(
            "বিনিয়োগ, রিবেট ও নিরাপদ সঞ্চয়ের চিত্র",
            "Investment, rebate and secure savings illustration"
        ),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun PagerIndicator(pageCount: Int, currentPage: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(width = if (index == currentPage) 22.dp else 9.dp, height = 9.dp)
                    .background(
                        color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            CalculatorSuccess.copy(alpha = 0.20f)
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
