package io.ak1.demo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType


@Composable
fun StoryVoyageTheme(
    themePreference: ThemePreference, content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDarkTheme = isSystemInDarkTheme()

    // Determine if dark mode should be used
    val useDarkTheme = when (themePreference.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.AUTO -> systemInDarkTheme
    }

    // Apply the appropriate color scheme
    val colorScheme = when {
        // For Android 12+ and Dynamic theme
        themePreference.themeType == ThemeType.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // For Static theme or older Android versions
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Set transparent status bar with appropriate icon color
//    TransparentSystemBars(darkTheme = useDarkTheme)

    val systemUiController = rememberSystemUiController()

    SideEffect {
        // Set transparent status bar
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = !useDarkTheme // Use light icons for dark theme, dark icons for light theme
        )

        // Set transparent navigation bar
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = !useDarkTheme, // Use light icons for dark theme, dark icons for light theme
            navigationBarContrastEnforced = false
        )
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, secondary = PurpleGrey80, tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, secondary = PurpleGrey40, tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)