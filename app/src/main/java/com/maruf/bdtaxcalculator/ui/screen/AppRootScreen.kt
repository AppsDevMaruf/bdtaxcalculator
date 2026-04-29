package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object TaxCalculator : Screen("tax_calculator")
    data object AuditChecker : Screen("audit_checker")
    data object Profile : Screen("profile")
}

enum class AppDestination {
    Home,
    TaxCalculator,
    AuditChecker,
    Profile
}

@Composable
fun AppRootScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            HomeBottomNavigation(
                selectedDestination = when {
                    currentRoute == Screen.Home.route -> AppDestination.Home
                    currentRoute == Screen.TaxCalculator.route -> AppDestination.TaxCalculator
                    currentRoute == Screen.AuditChecker.route -> AppDestination.AuditChecker
                    currentRoute == Screen.Profile.route -> AppDestination.Profile
                    else -> AppDestination.Home
                },
                onOpenHome = {
                    if (currentRoute != Screen.Home.route) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                onOpenTaxCalculator = {
                    if (currentRoute != Screen.TaxCalculator.route) {
                        navController.navigate(Screen.TaxCalculator.route)
                    }
                },
                onOpenAuditChecker = {
                    if (currentRoute != Screen.AuditChecker.route) {
                        navController.navigate(Screen.AuditChecker.route)
                    }
                },
                onOpenProfile = {
                    if (currentRoute != Screen.Profile.route) {
                        navController.navigate(Screen.Profile.route)
                    }
                }
            )
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
                    onOpenTaxCalculator = { navController.navigate(Screen.TaxCalculator.route) },
                    onOpenAuditChecker = { navController.navigate(Screen.AuditChecker.route) },
                    onOpenHome = { /* Already here */ },
                    onOpenProfile = { navController.navigate(Screen.Profile.route) },
                    selectedDestination = AppDestination.Home
                )
            }

            composable(Screen.TaxCalculator.route) {
                TaxCalculatorScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AuditChecker.route) {
                AuditCheckerScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
