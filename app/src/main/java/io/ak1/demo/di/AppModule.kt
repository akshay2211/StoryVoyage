package io.ak1.demo.di

import io.ak1.demo.data.preferences.ThemePreferencesDataSource
import io.ak1.demo.data.repository.AiAssistantRepositoryImpl
import io.ak1.demo.data.repository.ThemeRepositoryImpl
import io.ak1.demo.data.repository.VoiceRecognitionRepositoryImpl
import io.ak1.demo.data.source.AiAssistantDataSource
import io.ak1.demo.data.source.AiAssistantDataSourceImpl
import io.ak1.demo.data.source.VoiceRecognitionDataSource
import io.ak1.demo.data.source.VoiceRecognitionDataSourceImpl
import io.ak1.demo.domain.repository.AiAssistantRepository
import io.ak1.demo.domain.repository.ThemeRepository
import io.ak1.demo.domain.repository.VoiceRecognitionRepository
import io.ak1.demo.presentation.assistant.AiAssistantViewModel
import io.ak1.demo.presentation.home.HomeViewModel
import io.ak1.demo.presentation.settings.SettingsViewModel
import io.ak1.demo.presentation.theme.ThemeViewModel
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Dependency injection module for AI assistant and PDF viewer - Simplified MVI Architecture
 */
val appModule = module {
    // Data Sources
    single { ThemePreferencesDataSource(get()) }
    single<VoiceRecognitionDataSource> { VoiceRecognitionDataSourceImpl(get()) }
    factory<AiAssistantDataSource> { AiAssistantDataSourceImpl(get()) }

    // Repositories
    single<VoiceRecognitionRepository> { VoiceRecognitionRepositoryImpl(get()) }
    single<AiAssistantRepository> { AiAssistantRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }

    // ViewModels - All using simplified MVI pattern with direct repository access
    viewModelOf(::ThemeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::PdfViewerViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AiAssistantViewModel)
}
