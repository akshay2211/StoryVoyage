package io.ak1.demo.domain.model

// Domain layer entities
enum class ThemeType {
    STATIC,
    DYNAMIC
}

enum class ThemeMode {
    LIGHT,
    DARK,
    AUTO
}

data class ThemePreference(
    val themeType: ThemeType = ThemeType.STATIC,
    val themeMode: ThemeMode = ThemeMode.AUTO
)