package io.ak1.demo.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.ak1.demo.ui.screens.HomeScreen
import io.ak1.demo.ui.screens.PdfViewerScreen
import io.ak1.demo.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object PdfReader : Screen("pdf_reader/{pdfId}") {
        fun createRoute(pdfId: String): String = "pdf_reader/$pdfId"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController
) {
    LocalContext.current
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(
            route = Screen.PdfReader.route, arguments = listOf(
                navArgument("pdfId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pdfId = backStackEntry.arguments?.getString("pdfId") ?: ""
            PdfViewerScreen(pdfId, {
                navController.popBackStack()
            })
        }
    }
}