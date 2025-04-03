package io.ak1.demo.data.repository

import io.ak1.demo.data.preferences.ThemePreferencesDataSource
import io.ak1.demo.domain.model.ThemeMode
import io.ak1.demo.domain.model.ThemePreference
import io.ak1.demo.domain.model.ThemeType
import io.ak1.demo.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepositoryImpl(
    private val themePreferencesDataSource: ThemePreferencesDataSource
) : ThemeRepository {

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

    override suspend fun saveThemeType(themeType: ThemeType) {
        val dataType = when (themeType) {
            ThemeType.STATIC -> io.ak1.demo.data.preferences.ThemeType.STATIC
            ThemeType.DYNAMIC -> io.ak1.demo.data.preferences.ThemeType.DYNAMIC
        }
        themePreferencesDataSource.saveThemeType(dataType)
    }

    override suspend fun saveThemeMode(themeMode: ThemeMode) {
        val dataMode = when (themeMode) {
            ThemeMode.LIGHT -> io.ak1.demo.data.preferences.ThemeMode.LIGHT
            ThemeMode.DARK -> io.ak1.demo.data.preferences.ThemeMode.DARK
            ThemeMode.AUTO -> io.ak1.demo.data.preferences.ThemeMode.AUTO
        }
        themePreferencesDataSource.saveThemeMode(dataMode)
    }

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