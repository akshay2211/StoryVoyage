package io.ak1.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.compose.rememberNavController
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.navigation.AppNavigation
import io.ak1.demo.presentation.theme.ThemeViewModel
import io.ak1.demo.ui.theme.StoryVoyageTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

const val ipAddress = "192.168.1.9"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoinContext {
                ThemeApp()
            }
        }
    }
}

val LocalThemePrefs = staticCompositionLocalOf { ThemePreference() }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ThemeApp() {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val themePreference by themeViewModel.themePreference.collectAsState()
    CompositionLocalProvider(LocalThemePrefs provides themePreference) {
        StoryVoyageTheme(themePreference = themePreference) {
            SharedTransitionLayout {
                val navController = rememberNavController()
            AppNavigation(navController, this)
        }}
    }
}