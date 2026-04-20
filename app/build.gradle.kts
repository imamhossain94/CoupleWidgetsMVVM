plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.newagedevs.couplewidgets"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.newagedevs.couplewidgets"
        minSdk = 23
        targetSdk = 35
        versionCode = 18
        versionName = "1.1.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
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
}

dependencies {
    // android supports
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.appcompat:appcompat:1.7.1")

    // architecture components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // binding
    implementation("com.github.skydoves:bindables:1.2.0")

    // koin
    implementation("io.insert-koin:koin-android:2.2.3")
    implementation("io.insert-koin:koin-android-scope:2.2.3")
    implementation("io.insert-koin:koin-android-viewmodel:2.2.3")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // glide
    implementation("com.github.bumptech.glide:glide:5.0.5")
    kapt("com.github.bumptech.glide:compiler:5.0.5")

    // whatIf
    implementation("com.github.skydoves:whatif:1.2.1")

    // bundler
    implementation("com.github.skydoves:bundler:1.0.4")

    // debugging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // sheets
    implementation("com.maxkeppeler.sheets:color:2.3.1")
    implementation("com.maxkeppeler.sheets:calendar:2.3.1")
    implementation("com.maxkeppeler.sheets:option:2.3.1")

    // image picker
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.github.yalantis:ucrop:2.2.11")

    // gson
    implementation("com.google.code.gson:gson:2.13.2")

    // svg path kotlin
    implementation("com.github.slaviboy:SVGPathKotlin:0.3.0")

    // joda-time
    implementation("joda-time:joda-time:2.14.0")

    // Applovin
    implementation("com.applovin:applovin-sdk:13.6.0")
    implementation("androidx.lifecycle:lifecycle-process:2.10.0")

    // Google Play Core (Modular)
    implementation("com.google.android.play:review:2.0.2")
    implementation("com.google.android.play:review-ktx:2.0.2")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
