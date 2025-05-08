package io.ak1.demo.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.presentation.settings.SettingsIntent
import io.ak1.demo.presentation.settings.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel<SettingsViewModel>(),
    navTo:()-> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navTo.invoke() }) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            // Theme Type Card (Static/Dynamic)
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.processIntent(SettingsIntent.ToggleThemeOptions) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Theme Type",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    )
                    Text(
                        text = when (state.themePreference.themeType) {
                            ThemeType.STATIC -> "Static"
                            ThemeType.DYNAMIC -> "Dynamic"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Theme Type options with animation
            AnimatedVisibility(
                visible = state.isThemeOptionsExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .selectableGroup()
                ) {
                    ThemeOption(
                        text = "Static Theme",
                        icon = Icons.Default.InvertColors,
                        isSelected = state.themePreference.themeType == ThemeType.STATIC,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.SetThemeType(ThemeType.STATIC))
                        }
                    )

                    // Only show Dynamic option on Android 12+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ThemeOption(
                            text = "Dynamic Theme",
                            icon = Icons.Default.Palette,
                            isSelected = state.themePreference.themeType == ThemeType.DYNAMIC,
                            onClick = {
                                viewModel.processIntent(SettingsIntent.SetThemeType(ThemeType.DYNAMIC))
                            }
                        )
                    }
                }
            }

            // Theme Mode Card (Light/Dark/Auto)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = { viewModel.processIntent(SettingsIntent.ToggleThemeModeOptions) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (state.themePreference.themeMode) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                            ThemeMode.AUTO -> Icons.Default.Sync
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Theme Mode",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .weight(1f)
                    )
                    Text(
                        text = when (state.themePreference.themeMode) {
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                            ThemeMode.AUTO -> "Auto"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Theme Mode options with animation
            AnimatedVisibility(
                visible = state.isThemeModeOptionsExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .selectableGroup()
                ) {
                    ThemeOption(
                        text = "Light",
                        icon = Icons.Default.LightMode,
                        isSelected = state.themePreference.themeMode == ThemeMode.LIGHT,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.SetThemeMode(ThemeMode.LIGHT))
                        }
                    )

                    ThemeOption(
                        text = "Dark",
                        icon = Icons.Default.DarkMode,
                        isSelected = state.themePreference.themeMode == ThemeMode.DARK,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.SetThemeMode(ThemeMode.DARK))
                        }
                    )

                    ThemeOption(
                        text = "Auto (System)",
                        icon = Icons.Default.Sync,
                        isSelected = state.themePreference.themeMode == ThemeMode.AUTO,
                        onClick = {
                            viewModel.processIntent(SettingsIntent.SetThemeMode(ThemeMode.AUTO))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null  // null because we're handling clicks on the entire row
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(start = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}