// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

plugins {
    id("com.android.application") version "9.2.0" apply false
    id("com.android.library") version "9.2.0" apply false
    // Kotlin pinned to 2.3.20 — Kotlin 2.4.0 emits metadata version 2.4.0, which
    // Room 2.8.4's kapt annotation processor (kotlin-metadata-jvm) cannot yet parse.
    // Bump once Room ships a release compatible with Kotlin 2.4's metadata format.
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("com.android.legacy-kapt") version "9.2.0" apply false
}
