import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
}

android {
    namespace = "ru.chanramen.tgmemes.appmetrica_impl"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val secrets = Properties()
        File(rootDir, "secrets.properties").inputStream().use {
            secrets.load(it)
        }
        val appmetricaKey = secrets["appmetrica.key"]
        buildConfigField("String", "APPMETRICA_KEY", "\"$appmetricaKey\"")
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
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":analytics:api"))
    implementation(libs.appmetrica.analytics)
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
}