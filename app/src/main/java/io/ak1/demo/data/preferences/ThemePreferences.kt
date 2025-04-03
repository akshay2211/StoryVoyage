package io.ak1.demo.data.preferences


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Define theme types and modes
enum class ThemeType(val value: Int) {
    STATIC(0),
    DYNAMIC(1)
}

enum class ThemeMode(val value: Int) {
    LIGHT(0),
    DARK(1),
    AUTO(2)
}

data class ThemePreferenceModel(
    val themeType: ThemeType = ThemeType.STATIC,
    val themeMode: ThemeMode = ThemeMode.AUTO
)

class ThemePreferencesDataSource(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "theme_preferences"
        )
        private val THEME_TYPE_KEY = intPreferencesKey("theme_type")
        private val THEME_MODE_KEY = intPreferencesKey("theme_mode")
    }

    // Get the current theme preferences as Flow
    val themePreferences: Flow<ThemePreferenceModel> = context.dataStore.data.map { preferences ->
        val themeTypeValue = preferences[THEME_TYPE_KEY] ?: ThemeType.STATIC.value
        val themeModeValue = preferences[THEME_MODE_KEY] ?: ThemeMode.AUTO.value

        ThemePreferenceModel(
            themeType = ThemeType.entries.firstOrNull { it.value == themeTypeValue } ?: ThemeType.STATIC,
            themeMode = ThemeMode.entries.firstOrNull { it.value == themeModeValue } ?: ThemeMode.AUTO
        )
    }

    // Save theme type
    suspend fun saveThemeType(themeType: ThemeType) {
        context.dataStore.edit { preferences ->
            preferences[THEME_TYPE_KEY] = themeType.value
        }
    }

    // Save theme mode
    suspend fun saveThemeMode(themeMode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.value
        }
    }

    // Save both theme type and mode
    suspend fun saveThemePreferences(themePreferences: ThemePreferenceModel) {
        context.dataStore.edit { preferences ->
            preferences[THEME_TYPE_KEY] = themePreferences.themeType.value
            preferences[THEME_MODE_KEY] = themePreferences.themeMode.value
        }
    }
}