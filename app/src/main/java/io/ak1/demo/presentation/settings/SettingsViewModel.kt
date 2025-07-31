package io.ak1.demo.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Sealed class representing user intents for the Settings screen.
 */
sealed class SettingsIntent {
    data class SetThemeType(val themeType: ThemeType) : SettingsIntent()
    data class SetThemeMode(val themeMode: ThemeMode) : SettingsIntent()
    object ToggleThemeOptions : SettingsIntent()
    object ToggleThemeModeOptions : SettingsIntent()
}

/**
 * Represents the UI state for the Settings screen.
 * 
 * @param themePreference Current theme preferences (type and mode)
 * @param isThemeOptionsExpanded Whether the theme type selection is expanded
 * @param isThemeModeOptionsExpanded Whether the theme mode selection is expanded
 * @param isLoading Indicates if theme changes are being saved
 */
data class SettingsState(
    val themePreference: ThemePreference = ThemePreference(),
    val isThemeOptionsExpanded: Boolean = false,
    val isThemeModeOptionsExpanded: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * ViewModel for managing Settings screen functionality with MVI pattern.
 * 
 * This ViewModel handles theme customization settings including:
 * - Theme type selection (different visual themes)
 * - Theme mode selection (light/dark/auto)
 * - UI state management for expandable options
 * - Persistent storage of user preferences
 * 
 * @param themeRepository Repository for accessing and saving theme preferences
 */
class SettingsViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    /** Mutable state flow for managing Settings screen UI state */
    private val _state = MutableStateFlow(SettingsState())
    
    /** Exposed read-only state flow for UI observation */
    val state: StateFlow<SettingsState> = _state

    /**
     * Initializes the ViewModel by loading current theme preferences.
     */
    init {
        getThemePreferences()
    }

    /**
     * Observes theme preferences from the repository and updates the UI state.
     * Automatically reflects any external changes to theme preferences.
     */
    private fun getThemePreferences() {
        themeRepository.getThemePreferences()
            .onEach { preferences ->
                _state.update { currentState ->
                    currentState.copy(
                        themePreference = preferences,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Processes user intents using the MVI pattern.
     * Routes different types of user actions to appropriate handler methods.
     * 
     * @param intent The user intent to process
     */
    fun processIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.SetThemeType -> {
                setThemeType(intent.themeType)
            }
            is SettingsIntent.SetThemeMode -> {
                setThemeMode(intent.themeMode)
            }
            is SettingsIntent.ToggleThemeOptions -> {
                _state.update { it.copy(
                    isThemeOptionsExpanded = !it.isThemeOptionsExpanded,
                    // Close theme mode options if they are open and we're closing theme options
                    isThemeModeOptionsExpanded = if (!it.isThemeOptionsExpanded)
                        it.isThemeModeOptionsExpanded
                    else false
                ) }
            }
            is SettingsIntent.ToggleThemeModeOptions -> {
                _state.update { it.copy(isThemeModeOptionsExpanded = !it.isThemeModeOptionsExpanded) }
            }
        }
    }

    /**
     * Updates the theme type preference and saves it persistently.
     * 
     * @param themeType The new theme type to apply
     */
    private fun setThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            themeRepository.saveThemeType(themeType)
        }
    }

    /**
     * Updates the theme mode preference and saves it persistently.
     * 
     * @param themeMode The new theme mode to apply (Light, Dark, or Auto)
     */
    private fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            themeRepository.saveThemeMode(themeMode)
        }
    }
}