package io.ak1.demo.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.domain.usecase.ThemeUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// Define the MVI contract
sealed class SettingsIntent {
    data class SetThemeType(val themeType: ThemeType) : SettingsIntent()
    data class SetThemeMode(val themeMode: ThemeMode) : SettingsIntent()
    object ToggleThemeOptions : SettingsIntent()
    object ToggleThemeModeOptions : SettingsIntent()
}

data class SettingsState(
    val themePreference: ThemePreference = ThemePreference(),
    val isThemeOptionsExpanded: Boolean = false,
    val isThemeModeOptionsExpanded: Boolean = false,
    val isLoading: Boolean = false
)



class SettingsViewModel(
    private val themeUseCases: ThemeUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        getThemePreferences()
    }

    private fun getThemePreferences() {
        themeUseCases.getThemePreferences()
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

    private fun setThemeType(themeType: ThemeType) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            themeUseCases.saveThemeType(themeType)
        }
    }

    private fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            themeUseCases.saveThemeMode(themeMode)
        }
    }
}