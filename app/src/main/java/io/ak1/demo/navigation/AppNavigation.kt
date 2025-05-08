package io.ak1.demo.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.ak1.demo.ui.screens.DetailScreen
import io.ak1.demo.ui.screens.HomeScreen
import io.ak1.demo.ui.screens.PdfViewerScreen
import io.ak1.demo.ui.screens.ResourcesScreen
import io.ak1.demo.ui.screens.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Resources : Screen("resources")
    object Details : Screen("detail/{id}") {
        fun createRoute(id: String): String = "detail/$id"
    }

    object Reader : Screen("reader/{id}/{chat}") {
        fun createRoute(id: String, chat: Boolean = false): String = "reader/$id/$chat"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController, sharedTransitionScope: SharedTransitionScope
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(sharedTransitionScope, this) { navController.navigate(it) }
        }
        composable(Screen.Settings.route) {
            SettingsScreen { navController.navigateUp() }
        }
        composable(Screen.Resources.route) {
            ResourcesScreen { navController.navigateUp() }
        }
        composable(
            route = Screen.Details.route, arguments = listOf(
                navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            DetailScreen(id, sharedTransitionScope, this) {
                navController.navigate(it)
            }
        }
        composable(
            route = Screen.Reader.route,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("chat") { type = NavType.BoolType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            val chat = backStackEntry.arguments?.getBoolean("chat") == true
            PdfViewerScreen(id, chat, {
                navController.popBackStack()
            })
        }
    }
}