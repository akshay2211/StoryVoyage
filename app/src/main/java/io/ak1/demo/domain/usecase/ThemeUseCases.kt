package io.ak1.demo.domain.usecase

import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class GetThemePreferencesUseCase(private val repository: ThemeRepository) {
    operator fun invoke(): Flow<ThemePreference> {
        return repository.getThemePreferences()
    }
}

class SaveThemeTypeUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke(themeType: ThemeType) {
        repository.saveThemeType(themeType)
    }
}

class SaveThemeModeUseCase(private val repository: ThemeRepository) {
    suspend operator fun invoke(themeMode: ThemeMode) {
        repository.saveThemeMode(themeMode)
    }
}

data class ThemeUseCases(
    val getThemePreferences: GetThemePreferencesUseCase,
    val saveThemeType: SaveThemeTypeUseCase,
    val saveThemeMode: SaveThemeModeUseCase
)