package io.ak1.demo.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun TransparentSystemBars(
    darkTheme: Boolean
) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Set transparent status bar
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme // Use light icons for dark theme, dark icons for light theme
        )

        // Set transparent navigation bar
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !darkTheme, // Use light icons for dark theme, dark icons for light theme
            navigationBarContrastEnforced = false
        )
    }
}