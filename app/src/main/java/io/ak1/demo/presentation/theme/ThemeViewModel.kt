package io.ak1.demo.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.usecase.ThemeUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ThemeViewModel(
    private val themeUseCases: ThemeUseCases
) : ViewModel() {

    private val _themePreference = MutableStateFlow(ThemePreference())
    val themePreference: StateFlow<ThemePreference> = _themePreference

    init {
        getThemePreferences()
    }

    private fun getThemePreferences() {
        themeUseCases.getThemePreferences()
            .onEach { preferences ->
                _themePreference.value = preferences
            }
            .launchIn(viewModelScope)
    }
}