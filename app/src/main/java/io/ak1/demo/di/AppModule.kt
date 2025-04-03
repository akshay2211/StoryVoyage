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
import io.ak1.demo.domain.usecase.GetThemePreferencesUseCase
import io.ak1.demo.domain.usecase.InitializeAiAssistantUseCase
import io.ak1.demo.domain.usecase.SaveThemeModeUseCase
import io.ak1.demo.domain.usecase.SaveThemeTypeUseCase
import io.ak1.demo.domain.usecase.SendMessageUseCase
import io.ak1.demo.domain.usecase.TerminateAiAssistantUseCase
import io.ak1.demo.domain.usecase.ThemeUseCases
import io.ak1.demo.domain.usecase.voice.CancelVoiceRecognitionUseCase
import io.ak1.demo.domain.usecase.voice.StartVoiceRecognitionUseCase
import io.ak1.demo.domain.usecase.voice.StopVoiceRecognitionUseCase
import io.ak1.demo.presentation.assistant.AiAssistantViewModel
import io.ak1.demo.presentation.settings.SettingsViewModel
import io.ak1.demo.presentation.theme.ThemeViewModel
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Dependency injection module for AI assistant and PDF viewer
 */
val appModule = module {
    // Data Sources
    single { ThemePreferencesDataSource(get()) }
    single<VoiceRecognitionDataSource> { VoiceRecognitionDataSourceImpl(get()) }
    single<AiAssistantDataSource> { AiAssistantDataSourceImpl(get()) }

    // Repositories
    single<VoiceRecognitionRepository> { VoiceRecognitionRepositoryImpl(get()) }
    single<AiAssistantRepository> { AiAssistantRepositoryImpl(get()) }
    single<ThemeRepository> { ThemeRepositoryImpl(get()) }

    // Use cases
    factory { GetThemePreferencesUseCase(get()) }
    factory { SaveThemeModeUseCase(get()) }
    factory { SaveThemeTypeUseCase(get()) }
    factory { ThemeUseCases(get(), get(), get()) }

    // Use Cases - Voice Recognition
    factory { StartVoiceRecognitionUseCase(get()) }
    factory { StopVoiceRecognitionUseCase(get()) }
    factory { CancelVoiceRecognitionUseCase(get()) }

    // Use Cases - AI Assistant
    factory { SendMessageUseCase(get()) }
    factory { InitializeAiAssistantUseCase(get()) }
    factory { TerminateAiAssistantUseCase(get()) }

    // ViewModels
    viewModelOf(::ThemeViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::AiAssistantViewModel)
    viewModelOf(::PdfViewerViewModel)
}
