package io.ak1.demo.presentation.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private val _themePreference = MutableStateFlow(ThemePreference())
    val themePreference: StateFlow<ThemePreference> = _themePreference

    init {
        getThemePreferences()
    }

    private fun getThemePreferences() {
        themeRepository.getThemePreferences()
            .onEach { preferences ->
                _themePreference.value = preferences
            }
            .launchIn(viewModelScope)
    }
}