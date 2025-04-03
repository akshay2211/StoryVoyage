package io.ak1.demo

import android.app.Application
import com.pspdfkit.Nutrient
import io.ak1.demo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class ThemeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Nutrient.initialize(this@ThemeApplication,"Hello")

        // Initialize Koin
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@ThemeApplication)
            modules(appModule)
        }
    }
}