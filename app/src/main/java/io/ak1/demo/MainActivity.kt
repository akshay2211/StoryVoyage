package io.ak1.demo

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
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

const val ipAddress = "192.168.1.8"

class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KoinContext {
                ThemeApp { AppNavigation(rememberNavController(), it) }
            }
        }
    }
}

val LocalThemePrefs = staticCompositionLocalOf { ThemePreference() }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ThemeApp(content: @Composable (SharedTransitionScope) -> Unit) {
    val themeViewModel: ThemeViewModel = koinViewModel()
    val themePreference by themeViewModel.themePreference.collectAsState()
    CompositionLocalProvider(LocalThemePrefs provides themePreference) {
        StoryVoyageTheme(themePreference = themePreference) {
            SharedTransitionLayout {
                content.invoke(
                    this
                )
            }
        }
    }
}