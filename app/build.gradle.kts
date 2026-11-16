import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // KSP — generates Room code at compile time

    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"
}

// Secrets are read from local.properties, which is git-ignored, and injected
// into the generated BuildConfig class. Never commit real keys to VCS.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}

/**
 * Reads a value from local.properties, falling back to [defaultValue] when the
 * property is absent. Quotes and backslashes are escaped so the value can be
 * safely embedded into a generated Kotlin string literal.
 */
fun localProperty(key: String, defaultValue: String): String =
    (localProperties.getProperty(key) ?: defaultValue)
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")

android {
    namespace = "com.itis.itisandroid"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.itis.itisandroid"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // OpenWeatherMap credentials. Values come from local.properties:
        //   openweather.api.key=...
        //   openweather.base.url=https://api.openweathermap.org/data/2.5/
        // Generated as BuildConfig.OPENWEATHER_API_KEY / OPENWEATHER_BASE_URL
        buildConfigField(
            "String",
            "OPENWEATHER_API_KEY",
            "\"${localProperty("openweather.api.key", "")}\"",
        )
        buildConfigField(
            "String",
            "OPENWEATHER_BASE_URL",
            "\"${localProperty("openweather.base.url", "https://api.openweathermap.org/data/2.5/")}\"",
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        // Generates the BuildConfig class with the fields declared in defaultConfig
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    /* Coil */
    implementation(libs.coil.compose)
    implementation(libs.coil)

    /* compose */
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    /* navigation */
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    /* room (database) */
    // Runtime library: Database, Entity, DAO annotations
    implementation(libs.androidx.room.runtime)
    // Coroutines/Flow support: DAO methods return Flow, suspend functions
    implementation(libs.androidx.room.ktx)
    // Room compiler: runs as a KSP processor, generates implementation code
    ksp(libs.androidx.room.compiler)

    /* retrofit (network / weather feature) */
    // Retrofit: declares the API as a Kotlin interface
    implementation(libs.retrofit)
    // Gson converter: parses the JSON response into Kotlin objects
    implementation(libs.converter.gson)
    // Gson: JSON parser
    implementation(libs.gson)


    /* testing */
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
