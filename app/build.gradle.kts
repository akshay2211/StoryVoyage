plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.ak1.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.ak1.demo"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    // Navigation Compose
    implementation(libs.androidx.navigation.compose)
    // DataStore Preferences
    implementation(libs.androidx.datastore.preferences)
    // Koin for Dependency Injection
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.navigation)
    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)
    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // System UI Controller
    implementation(libs.accompanist.systemuicontroller)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Compose Pager and Animation
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.animation.graphics)

    // Nutrient SDK
    implementation(libs.nutrient)
    implementation(libs.androidx.ui.text.google.fonts)

    // Markwon (Markdown rendering).
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:html:4.6.2")
    implementation("io.noties.markwon:linkify:4.6.2")
    implementation("io.noties.markwon:ext-tables:4.6.2")
    implementation("io.noties.markwon:ext-tasklist:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")

    // Socket.IO
    implementation("io.socket:socket.io-client:2.1.1")

    // Json Web Tokens
    implementation("io.github.nefilim.kjwt:kjwt-core:0.9.0")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Http logging.
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Lottie
    implementation("com.airbnb.android:lottie-compose:6.0.1")
    implementation("com.github.LottieFiles:dotlottie-android:0.5.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}