package io.ak1.demo.domain.repository

import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemePreferences(): Flow<ThemePreference>
    suspend fun saveThemeType(themeType: ThemeType)
    suspend fun saveThemeMode(themeMode: ThemeMode)
    suspend fun saveThemePreferences(themePreferences: ThemePreference)
}