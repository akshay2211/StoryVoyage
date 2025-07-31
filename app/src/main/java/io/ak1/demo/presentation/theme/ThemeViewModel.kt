package io.ak1.demo.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * ViewModel for managing global theme preferences and state.
 * 
 * This ViewModel provides a centralized way to handle theme preferences
 * across the entire application. It automatically observes theme changes
 * from the repository and exposes them to the UI layer.
 * 
 * Unlike other ViewModels in the app, this doesn't follow the full MVI pattern
 * as theme changes are primarily reactive and don't require complex user intents.
 * 
 * @param themeRepository Repository for accessing and managing theme preferences
 */
class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    /** Mutable state flow for managing theme preferences */
    private val _themePreference = MutableStateFlow(ThemePreference())
    
    /** Exposed read-only state flow for theme preferences observation */
    val themePreference: StateFlow<ThemePreference> = _themePreference

    /**
     * Initializes the ViewModel by automatically loading current theme preferences.
     */
    init {
        getThemePreferences()
    }

    /**
     * Observes theme preferences from the repository and updates the UI state.
     * This creates a continuous flow of theme preference updates that automatically
     * reflect any changes made through the settings screen.
     */
    private fun getThemePreferences() {
        themeRepository.getThemePreferences()
            .onEach { preferences ->
                _themePreference.value = preferences
            }
            .launchIn(viewModelScope)
    }
}