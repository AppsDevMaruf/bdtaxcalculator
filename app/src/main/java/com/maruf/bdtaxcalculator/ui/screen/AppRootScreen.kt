package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.maruf.bdtaxcalculator.firebase.FirebaseTracker

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object TaxCalculator : Screen("tax_calculator")
    data object AuditChecker : Screen("audit_checker")
    data object NbrTinCheck : Screen("nbr_tin_check")
    data object TaxFaq : Screen("tax_faq")
    data object Profile : Screen("profile")
}

enum class AppDestination {
    Home,
    TaxCalculator,
    AuditChecker,
    Profile
}

private fun NavHostController.navigateToBottomTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppRootScreen(
    language: String,
    themeMode: String,
    onLanguageChange: (String) -> Unit,
    onThemeModeChange: (String) -> Unit,
    themePalette: String,
    onThemePaletteChange: (String) -> Unit,
    onRequestInAppReview: (String) -> Unit,
    onOpenStoreListing: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val density = LocalDensity.current
    val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0

    LaunchedEffect(currentRoute) {
        val screenName = when (currentRoute) {
            Screen.TaxCalculator.route -> "tax_calculator"
            Screen.AuditChecker.route -> "audit_checker"
            Screen.NbrTinCheck.route -> "nbr_tin_check"
            Screen.TaxFaq.route -> "tax_faq"
            Screen.Profile.route -> "settings"
            else -> "home"
        }
        FirebaseTracker.logScreen(screenName)
    }

    Scaffold(
        bottomBar = {
            if (!isKeyboardOpen) {
                HomeBottomNavigation(
                    selectedDestination = when (currentRoute) {
                        Screen.TaxCalculator.route -> AppDestination.TaxCalculator
                        Screen.AuditChecker.route -> AppDestination.AuditChecker
                        Screen.Home.route -> AppDestination.Home
                        Screen.Profile.route -> AppDestination.Profile
                        else -> AppDestination.Home
                    },
                    onOpenHome = {
                        if (currentRoute != Screen.Home.route) {
                            navController.navigateToBottomTab(Screen.Home.route)
                        }
                    },
                    onOpenTaxCalculator = {
                        if (currentRoute != Screen.TaxCalculator.route) {
                            navController.navigateToBottomTab(Screen.TaxCalculator.route)
                        }
                    },
                    onOpenAuditChecker = {
                        if (currentRoute != Screen.AuditChecker.route) {
                            navController.navigateToBottomTab(Screen.AuditChecker.route)
                        }
                    },
                    onOpenProfile = {
                        if (currentRoute != Screen.Profile.route) {
                            navController.navigateToBottomTab(Screen.Profile.route)
                        }
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onOpenTaxCalculator = { navController.navigateToBottomTab(Screen.TaxCalculator.route) },
                    onOpenAuditChecker = { navController.navigateToBottomTab(Screen.AuditChecker.route) },
                    onOpenNbrTinCheck = { navController.navigate(Screen.NbrTinCheck.route) },
                    onOpenTaxFaq = { navController.navigate(Screen.TaxFaq.route) },
                    onOpenHome = { /* Already here */ },
                    onOpenProfile = { navController.navigateToBottomTab(Screen.Profile.route) },
                    selectedDestination = AppDestination.Home
                )
            }

            composable(Screen.TaxCalculator.route) {
                TaxCalculatorScreen(
                    onRequestInAppReview = onRequestInAppReview
                )
            }

            composable(Screen.AuditChecker.route) {
                AuditCheckerScreen(
                    onRequestInAppReview = onRequestInAppReview
                )
            }

            composable(Screen.NbrTinCheck.route) {
                NbrTinCheckScreen()
            }

            composable(Screen.TaxFaq.route) {
                TaxFaqScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    language = language,
                    themeMode = themeMode,
                    onLanguageChange = onLanguageChange,
                    onThemeModeChange = onThemeModeChange,
                    themePalette = themePalette,
                    onThemePaletteChange = onThemePaletteChange,
                    onRateApp = onOpenStoreListing
                )
            }
        }
    }
}
