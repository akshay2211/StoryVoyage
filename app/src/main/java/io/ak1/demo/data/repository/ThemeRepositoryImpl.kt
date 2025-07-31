package io.ak1.demo.data.repository

import io.ak1.demo.data.preferences.ThemePreferencesDataSource
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of ThemeRepository that handles theme preference persistence.
 * 
 * This repository acts as a bridge between the domain layer and data layer,
 * providing theme management functionality including:
 * - Reactive theme preference observation
 * - Individual theme type and mode updates
 * - Complete theme preference persistence
 * - Domain-to-data model mapping and conversion
 * 
 * The repository handles the mapping between domain models (used by ViewModels)
 * and data models (used by DataStore), ensuring proper separation of concerns.
 * 
 * @param themePreferencesDataSource Data source for accessing theme preferences storage
 */
class ThemeRepositoryImpl(
    private val themePreferencesDataSource: ThemePreferencesDataSource
) : ThemeRepository {

    /**
     * Provides a reactive stream of theme preferences.
     * Maps data layer models to domain models for consumption by ViewModels.
     * 
     * @return Flow of ThemePreference domain models
     */
    override fun getThemePreferences(): Flow<ThemePreference> {
        return themePreferencesDataSource.themePreferences.map { dataModel ->
            ThemePreference(
                themeType = when (dataModel.themeType) {
                    io.ak1.demo.data.preferences.ThemeType.STATIC -> ThemeType.STATIC
                    io.ak1.demo.data.preferences.ThemeType.DYNAMIC -> ThemeType.DYNAMIC
                },
                themeMode = when (dataModel.themeMode) {
                    io.ak1.demo.data.preferences.ThemeMode.LIGHT -> ThemeMode.LIGHT
                    io.ak1.demo.data.preferences.ThemeMode.DARK -> ThemeMode.DARK
                    io.ak1.demo.data.preferences.ThemeMode.AUTO -> ThemeMode.AUTO
                }
            )
        }
    }

    /**
     * Saves the theme type preference.
     * Converts domain model to data model before persistence.
     * 
     * @param themeType The theme type to save (STATIC or DYNAMIC)
     */
    override suspend fun saveThemeType(themeType: ThemeType) {
        val dataType = when (themeType) {
            ThemeType.STATIC -> io.ak1.demo.data.preferences.ThemeType.STATIC
            ThemeType.DYNAMIC -> io.ak1.demo.data.preferences.ThemeType.DYNAMIC
        }
        themePreferencesDataSource.saveThemeType(dataType)
    }

    /**
     * Saves the theme mode preference.
     * Converts domain model to data model before persistence.
     * 
     * @param themeMode The theme mode to save (LIGHT, DARK, or AUTO)
     */
    override suspend fun saveThemeMode(themeMode: ThemeMode) {
        val dataMode = when (themeMode) {
            ThemeMode.LIGHT -> io.ak1.demo.data.preferences.ThemeMode.LIGHT
            ThemeMode.DARK -> io.ak1.demo.data.preferences.ThemeMode.DARK
            ThemeMode.AUTO -> io.ak1.demo.data.preferences.ThemeMode.AUTO
        }
        themePreferencesDataSource.saveThemeMode(dataMode)
    }

    /**
     * Saves complete theme preferences including both type and mode.
     * Converts domain model to data model before persistence.
     * 
     * @param themePreferences Complete theme preferences to save
     */
    override suspend fun saveThemePreferences(themePreferences: ThemePreference) {
        val dataModel = io.ak1.demo.data.preferences.ThemePreferenceModel(
            themeType = when (themePreferences.themeType) {
                ThemeType.STATIC -> io.ak1.demo.data.preferences.ThemeType.STATIC
                ThemeType.DYNAMIC -> io.ak1.demo.data.preferences.ThemeType.DYNAMIC
            },
            themeMode = when (themePreferences.themeMode) {
                ThemeMode.LIGHT -> io.ak1.demo.data.preferences.ThemeMode.LIGHT
                ThemeMode.DARK -> io.ak1.demo.data.preferences.ThemeMode.DARK
                ThemeMode.AUTO -> io.ak1.demo.data.preferences.ThemeMode.AUTO
            }
        )
        themePreferencesDataSource.saveThemePreferences(dataModel)
    }
}